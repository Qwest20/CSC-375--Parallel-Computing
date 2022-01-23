import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class PlaySession extends Thread {

    // winning game state
    private final int[][] winningState = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};

    // private wariables
    private final String name;
    private final Twiddle game;
    private final int maxMoves;
    private final int messUpConstant;
    private final ReentrantLock lock;
    private final ArrayList<String> moves = new ArrayList<>();

    // constructor
    public PlaySession(String n, int[][] start, int mM, ReentrantLock r, int mUC) {
        this.name = n;
        this.game = new Twiddle(start);
        this.maxMoves = mM;
        this.messUpConstant = mUC;
        // references
        this.lock = r;
    }

    // run method
    @Override
    public void run() {

        // int i used to keep track of how many moves this thread has used so far
        int i = 0;
        // final check condition
        while (i < maxMoves && this.game.getMapping() != winningState) {

            // a randomly generated integer decides the following boolean outcome. If true, we mess up
            int chance = ThreadLocalRandom.current().nextInt(0, 10);
            boolean messUp = chance < messUpConstant;
            
            // store results for later in a 2D array of unspecified Objects
            // (My workaround to return 2 pieces of data ;D)
            Object[] results = {"", 0};

            // add a one-second delay so our program takes a bit longer to process a move
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }

            if (!messUp) {
                //stage 2+
                if (this.game.getMapping()[0][0] == 1) {
                    //stage 3+
                    if (this.game.getMapping()[0][1] == 2) {
                        //stage 4+
                        if (this.game.getMapping()[0][2] == 3) {
                            //stage 5+
                            if (this.game.getMapping()[2][0] == 7) {
                                //stage 6+
                                if (this.game.getMapping()[2][1] == 8) {
                                    //stage 7+
                                    if (this.game.getMapping()[2][2] == 9) {
                                        //stage 8+
                                        if (this.game.getMapping()[1][1] == 5) {
                                            // stage 8 logic
                                            if (this.game.getMapping()[1][0] == 4) {
                                                // we win!
                                                break;
                                            }
                                            // stage 8 logic
                                            else {
                                                results = this.game.stage8Logic();
                                                moves.add(results[0].toString());
                                            }
                                        }
                                        // stage 7 logic
                                        else {
                                            results = this.game.stage7Logic();
                                            moves.add(results[0].toString());
                                        }
                                    }
                                    // stage 6 logic
                                    else {
                                        results = this.game.stage6Logic();
                                        moves.add(results[0].toString());
                                    }
                                }
                                // stage 5 logic
                                else {
                                    results = this.game.stage5Logic();
                                    moves.add(results[0].toString());
                                }
                            }
                            // stage 4 logic
                            else {
                                results = this.game.stage4Logic();
                                moves.add(results[0].toString());
                            }
                        }
                        // stage 3 logic
                        else {
                            results = this.game.stage3Logic();
                            moves.add(results[0].toString());
                        }
                    }
                    // stage 2 logic
                    else {
                        results = this.game.stage2Logic();
                        moves.add(results[0].toString());
                    }
                }
                // stage 1 logic
                else {
                    results = this.game.stage1Logic();
                    moves.add(results[0].toString());
                }
            }
            // messup move
            else {
                results = this.game.messUpMove();
                moves.add(results[0].toString());
            }
            i+=Integer.parseInt(results[1].toString());
        }

        // if we are out of this loop, then we have found our first solution. We will now lock and then shutdown
        lock.lock();

        // revise the arraylist of moves here, so we have individual moves at each step
        // (not bundles of algorithm sequences)
        ArrayList<String> revised = new ArrayList<>();
        for (String move : moves) {
            // this moves has multiple moves so to speak
            if (move.contains(",")) {
                String[] movements = move.split(",");
                revised.addAll(Arrays.asList(movements));
            } else
                revised.add(move);
        }

        // reached maximum moves
        if(i>=maxMoves) {
            if (i > maxMoves) {
                for (int j = 0; j < i - maxMoves; j++) {
                    revised.remove(revised.get(revised.size() - 1));
                }
            }
            System.out.println(name + " has our result at maximum moves (may not have completely solved...)");
        }
        else
            System.out.println(name + " has our winning result!");
        Results.winner = this.getName();
        Results.winningMoves = revised;
    }
}
