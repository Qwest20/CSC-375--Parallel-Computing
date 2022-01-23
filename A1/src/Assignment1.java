import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class Assignment1 {

    public static void main(String[] args) {

        // globals to mess around with the overall program execution
        int numRooms = 32;
        int numLoops = 101;
        // specify number of GUIs based on the number of loops you entered
        // ex. 10 GUIs in 101 loops = a GUI at every 10th room
        int numGUIs = 10;

        // Call an instance of our Rooms class, so we may refer to the room ArrayList across all the threads
        Rooms r = new Rooms();
        // A collection of rooms to work with is initialized within the Rooms class
        for (int i = 0; i < numRooms; i++) {
            Rooms.listOfRooms.add(new Room());
        }

        // Objects for Concurrent Computing
        // The Executor Service will handle our thread creation and allocation to the requested tasks
        ExecutorService executorService = Executors.newFixedThreadPool(numRooms);
        // The Phaser will halt the work of the threads such that we can complete a final task before moving on to the next iteration
        // This particular task is making our GUI display
        Phaser ph = new Phaser(1);

        // our algorithm over the specified number of iterations/loops
        for (int count = 0; count < numLoops; count++) {
            
            // OUR GENETIC ALGORITHM WITH CONCURRENT PROGRAMMING IN MIND:

            // Run optimization and half room swapping code in concurrent fashion!
            for (int i = 0; i < numRooms; i++) {
                executorService.execute(new RoomTask("thread-" + i, r, i, ph));
            }

            // sort Rooms by max to min with the main thread
            for (int i = 1; i < Rooms.listOfRooms.size(); i++) {

                // use this to reference the element behind in the arraylist, since we can overtake it with this element
                int j = i;
                // use this to see if this room can be moved up in the collection based on its affinity value
                boolean moveUp = false;

                // get a reference to this room's affinity:
                int thisAfin = Rooms.listOfRooms.get(i).getAffinity();

                // iterate backwards on j to see if we can get this particular room higher in the ArrayList
                while (j > 0 && thisAfin > Rooms.listOfRooms.get(j - 1).getAffinity()) {
                    // move up the list since we can effectively overtake this one
                    j--;
                    moveUp = true;
                }
                if (moveUp) {
                    // delete the checked element and place it at j since we know it can be moved
                    Room temp = Rooms.listOfRooms.get(i);
                    Rooms.listOfRooms.remove(Rooms.listOfRooms.get(i));
                    Rooms.listOfRooms.add(j, temp);
                }
            }

            // GUI STUFF GOES HERE, since we are almost into the next phase and all the threads
            // are waiting for the next iteration at this point
            if(count % (numLoops/numGUIs) == 0){
                // wait 5 seconds and then display the GUI at this stage of development
                try {
                    Thread.sleep(5000);
                    BestRoomGUI brGUI = new BestRoomGUI(800,800,Rooms.listOfRooms.get(0), count);
                    brGUI.setUpGUI();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // complete this phase and begin the next one, since all the worker threads are all done as well
            ph.arriveAndAwaitAdvance();
        }

        // do this at the end of your program, since we're all set with the phaser
        ph.arriveAndDeregister();

        // handle the executor service
        executorService.shutdown();
        try {
            // wait 8 seconds before termination
            if (!executorService.awaitTermination(8000, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}

// Make a whole class for the shared public static resource. This allows us to refer to it as desired between the threads.
class Rooms {
    public static ArrayList<Room> listOfRooms = new ArrayList<>();
}

class RoomTask extends Thread{

    // private wariables, including reference to the very same Room, Index, and Phaser that we utilize in the main method
    private final String threadName;
    private final Rooms r;
    private final Phaser ph;
    private final int index;

    // Constructor
    RoomTask(String threadName, Rooms r, int i, Phaser ph) {
        this.threadName = threadName;
        this.r = r;
        this.index = i;
        this.ph = ph;
        // register this thread on the phaser, so we can monitor its progress alongside other threads
        ph.register();
    }

    @Override
    public void run() {
        // work on the arraylist of rooms here in the synchronized block to prevent data races
        synchronized (r){
            // optimize the room that this thread is assigned to
            Rooms.listOfRooms.get(index).optimizeRoom();

            // swap with the next room in the array
            // do this on every room except the last
            // this will allow the best half rooms to gradually float up the ArrayList of whole Rooms
            if (index < Rooms.listOfRooms.size() - 1) {
                // Update this code. Only swap top rooms or bottom rooms.
                // can we swap top rooms?
                if (Rooms.listOfRooms.get(index).getHalfA().getAffinity() < Rooms.listOfRooms.get(index + 1).getHalfA().getAffinity()) {
                    HalfRoom temp = Rooms.listOfRooms.get(index + 1).getHalfA();
                    Rooms.listOfRooms.get(index + 1).setHalfA(Rooms.listOfRooms.get(index).getHalfA());
                    Rooms.listOfRooms.get(index).setHalfA(temp);
                }
                // can we swap bottom rooms instead?
                else if (Rooms.listOfRooms.get(index).getHalfB().getAffinity() < Rooms.listOfRooms.get(index + 1).getHalfB().getAffinity()) {
                    HalfRoom temp = Rooms.listOfRooms.get(index + 1).getHalfB();
                    Rooms.listOfRooms.get(index + 1).setHalfB(Rooms.listOfRooms.get(index).getHalfB());
                    Rooms.listOfRooms.get(index).setHalfB(temp);
                }
            }
            // refresh the data from the previous loop in this current room before processing
            // we also update the whole room's affinity here
            Rooms.listOfRooms.get(index).refreshWholeRoom();
        }
        // mark that we're ready to continue
        ph.arriveAndAwaitAdvance();
        // All threads have completed their work. Move along.
        ph.arriveAndDeregister();
    }
}