import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

// Your Cluster Host (Run in Terminal A)
public class ServerRho {


    private static TwiddleDisplayGUI tdg;

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        // Display Print to show which Node this is
        System.out.println("  ____                                \n" +
                " / ___|   ___  _ __ __   __ ___  _ __ \n" +
                " \\___ \\  / _ \\| '__|\\ \\ / // _ \\| '__|\n" +
                "  ___) ||  __/| |    \\ V /|  __/| |   \n" +
                " |____/  \\___||_|     \\_/  \\___||_| \n");

        // start with a shuffled array of 2D ints that will be referred to across all the clients
        final int[][] start = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        // swap the elements 3 times over to really make sure we're really shuffled
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    // generate new coordinates
                    int l = (int) (Math.random() * 2);
                    int m = (int) (Math.random() * 2);
                    //swapping
                    int temp = start[j][k];
                    start[j][k] = start[l][m];
                    start[l][m] = temp;
                }
            }
        }
        // Send the Client the top level shared information
        // global numThreads and numMoves modifiable values
        int numMoves = 30;
        int numThreads = 128;
        // mess up constant (as this increases, mess up likelihood increases by a factor of 10% (choose 0-9 for this))
        int messUpConstant = 6;

        // don't need to specify a hostname, it will be the current machine
        ServerSocket ss = new ServerSocket(7777);

        // Call to wait for three client Sockets (will iteratively wait until we have three ClientSockets)
        Socket[] ClientSockets = new Socket[3];
        ArrayList<ArrayList<String>> ClientResults = new ArrayList<>();
        String[] nodeOrder = new String[3];

        // Expects 3 socket connections before making a final evaluation
        for (int i = 0; i < 3; i++) {

            System.out.println("ServerSocket awaiting for connection :" + i);

            // add this new client connection to the end of the list, so they're in order
            ClientSockets[i] = ss.accept();
            System.out.println("Connection from " + ClientSockets[i] + "!");

            // get the output stream from the socket.
            OutputStream outputStream = ClientSockets[i].getOutputStream();
            // create an object output stream from the output stream, so we can send an object through it
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            // an array of parameters to send to each of the clients
            Object[] params = {numMoves, numThreads, messUpConstant, start};
            objectOutputStream.writeObject(params);

            // CLIENT DOES SOME STUFF...

            // get the input stream from the connected socket (needs to be created before the object input stream)
            InputStream inputStream = ClientSockets[i].getInputStream();
            // create a DataInputStream, so we can read data from it.
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            System.out.println("Received Return Object from " + ClientSockets[i] + "!");

            ArrayList<String> winningMoves = (ArrayList<String>) objectInputStream.readObject();
            ClientResults.add(ClientResults.size(), winningMoves);
            String nodeName = objectInputStream.readObject().toString();
            nodeOrder[i] = nodeName;
        }

        // we're done with the sockets! Let's dip!
        System.out.println("Closing sockets.");
        ss.close();
        for (Socket clientSocket : ClientSockets) {
            clientSocket.close();
        }

        // Navigate the Arraylist of results to find the best result!
        // Hold reference to the node and thread within that node!
        ArrayList<String> trueWinner = new ArrayList<>();
        String nameOfTrueWinner = "";
        for (int i = 0; i < ClientResults.size(); i++) {
            if (ClientResults.get(i).size() < trueWinner.size() ||
                    trueWinner.size() == 0) {
                trueWinner = ClientResults.get(i);
                // provides the name of the node at this particular index
                nameOfTrueWinner = nodeOrder[i];
            }
        }
        // final output as GUI
        TwiddleDisplayGUI tdg = new TwiddleDisplayGUI(900, 900, start, nameOfTrueWinner,
                trueWinner, numMoves, numThreads);
        tdg.setUpGUI();
    }
}

// Server output
/*
ServerSocket awaiting connections...
Connection from Socket[addr=/127.0.0.1,port=62360,localport=7777]!
Received [4] messages from: Socket[addr=/127.0.0.1,port=62360,localport=7777]
All messages:
Hello from the other side!
How are you doing?
What time is it?
Hi hi hi hi.
Closing sockets.
*/

// Client output
/*
Connected!
Sending messages to the ServerSocket
Closing socket and terminating program.
*/