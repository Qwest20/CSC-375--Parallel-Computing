import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

// The Cluster Client will run a portion of our code for us
public class ClientWolf {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        // Display Print to show which Node this is
        System.out.println("  ____  _  _               _                            __        __      _   __ \n" +
                "  / ___|| |(_)  ___  _ __  | |_        ___   _ __        \\ \\      / /___  | | / _|\n" +
                " | |    | || | / _ \\| '_ \\ | __|      / _ \\ | '_ \\        \\ \\ /\\ / // _ \\ | || |_ \n" +
                " | |___ | || ||  __/| | | || |_      | (_) || | | |        \\ V  V /| (_) || ||  _|\n" +
                "  \\____||_||_| \\___||_| |_| \\__|      \\___/ |_| |_|         \\_/\\_/  \\___/ |_||_|  ");

        // need host and port, we want to connect to the ServerSocket at port 7777
        Socket socket = new Socket("129.3.20.24", 7777);
        System.out.println("Connected!");

        // get the input stream from the connected socket (needs to be created before the object input stream)
        InputStream inputStream = socket.getInputStream();
        // create a DataInputStream, so we can read data from it.
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        Object[] params = (Object[]) objectInputStream.readObject();

        // Run Assignment 3 here
        int numMoves = (int) params[0];
        int numThreads = (int) params[1];
        int messUpConstant = (int) params[2];
        int[][] startState = (int[][]) params[3];
        A3MainProgram twiddleSolver = new A3MainProgram(numMoves, numThreads, messUpConstant, startState);
        twiddleSolver.run();
        ArrayList<String> moves = twiddleSolver.getWinningMoves();
        System.out.println("Length of moves: "+moves.size());

        // get the output stream from the socket.
        OutputStream outputStream = socket.getOutputStream();
        // create an object output stream from the output stream, so we can send an object through it
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        System.out.println("Sending Best Twiddle Result to the ServerSocket");
        objectOutputStream.writeObject(moves);
        System.out.println("Sending Name to the ServerSocket");
        objectOutputStream.writeObject("Wolf");

        System.out.println("Closing socket and terminating program.");
        socket.close();
        System.exit(0);
    }
}
