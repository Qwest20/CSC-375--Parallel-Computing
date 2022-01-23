import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class A3MainProgram {

    // VARIABLES FOR OUTPUT
    private String winningThread;
    private ArrayList<String> winningMoves;

    // global numThreads and numMoves modifiable values
    private final int numMoves;
    private final int numThreads;
    // mess up constant (as this increases, mess up likelihood increases by a factor of 10% (choose 0-9 for this))
    private final int messUpConstant;
    private final int[][] start;

    // Constructor
    public A3MainProgram(int nM, int nT, int mUC, int[][] s){
        this.numMoves = nM;
        this. numThreads = nT;
        this.messUpConstant = mUC;
        this.start = s;
    }

    // FINAL RESULT GETTERS
    public String getWinningThread() {
        return winningThread;
    }
    public ArrayList<String> getWinningMoves() {
        return winningMoves;
    }

    // run the original program
    public void run() {

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

        // save the results of this run
        this.winningThread = Results.winner;
        this.winningMoves = Results.winningMoves;
    }
}
