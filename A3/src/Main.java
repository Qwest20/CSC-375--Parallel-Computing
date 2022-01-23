import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {

        // global numThreads and numMoves modifiable values
        int numMoves = 30;
        int numThreads = 128;
        // mess up constant (as this increases, mess up likelihood increases by a factor of 10% (choose 0-9 for this))
        int messUpConstant = 6;

        // start with a shuffled array of 2D ints
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

        // Objects for Parallel Computing
        // The Executor Service will handle our thread creation
        ExecutorService SuzyGreenberg = Executors.newFixedThreadPool(numThreads);

        // Reentrant Lock for the threads
        ReentrantLock r = new ReentrantLock();

        // boolean to keep track of early shut down
        boolean earlyShutdown = false;

        // Loop through the threads
        for (int j = 0; j < numThreads; j++) {

            // add a delay so our program takes time in making new threads
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // do we have a result before all the threads are established?
            if (Results.winningMoves.size() > 0) {
                SuzyGreenberg.shutdownNow();
                System.out.println("SHUTDOWN ENGAGED");
                // we have a solution already! We won't enter the while loop below
                earlyShutdown = true;
                break;
                // if not, make a new worker thread
            } else {
                SuzyGreenberg.execute(new PlaySession("PlayerSession #" + j, start, numMoves, r, messUpConstant));
                System.out.println("PlayerSession #" + j + " began running");
            }
        }

        // enter this if we made all the threads and still need to check for a solution to be found
        if (!earlyShutdown) {
            while (Results.winningMoves.size() == 0) {
                // waiting for completion...
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Awaiting completion");
            }
            // extra shut down if needed
            SuzyGreenberg.shutdownNow();
            System.out.println("SHUTDOWN ENGAGED");
        }

        // final output as GUI
        TwiddleDisplayGUI tdg = new TwiddleDisplayGUI(900, 900, start,
                Results.winner, Results.winningMoves, numMoves, numThreads);
        tdg.setUpGUI();
    }
}
