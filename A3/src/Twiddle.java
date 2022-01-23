import java.util.concurrent.ThreadLocalRandom;

public class Twiddle {

    // basic game setup as a 3x3 2D array
    private final int[][] twiddleGame = new int[3][3];

    // Constructor
    public Twiddle(int[][] start) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                twiddleGame[i][j] = start[i][j];
            }
        }
    }

    // getters
    public int[][] getMapping() {
        return this.twiddleGame;
    }

    // normal (CW) rotations (just swapping in particular directions and locations)
    public void rotateD() {
        int tL = this.twiddleGame[1][1];
        int tR = this.twiddleGame[1][2];
        int bL = this.twiddleGame[2][1];
        int bR = this.twiddleGame[2][2];
        //[1][1] -> [1][2]
        this.twiddleGame[1][2] = tL;
        //[1][2] -> [2][2]
        this.twiddleGame[2][2] = tR;
        //[2][2] -> [2][1]
        this.twiddleGame[2][1] = bR;
        //[2][1] -> [1][1]
        this.twiddleGame[1][1] = bL;
    }

    public void rotateC() {
        int tL = this.twiddleGame[1][0];
        int tR = this.twiddleGame[1][1];
        int bL = this.twiddleGame[2][0];
        int bR = this.twiddleGame[2][1];
        //[1][0] -> [1][1]
        this.twiddleGame[1][1] = tL;
        //[1][1] -> [2][1]
        this.twiddleGame[2][1] = tR;
        //[2][1] -> [2][0]
        this.twiddleGame[2][0] = bR;
        //[2][0] -> [1][0]
        this.twiddleGame[1][0] = bL;
    }

    public void rotateB() {
        int tL = this.twiddleGame[0][1];
        int tR = this.twiddleGame[0][2];
        int bL = this.twiddleGame[1][1];
        int bR = this.twiddleGame[1][2];
        //[0][1] -> [0][2]
        this.twiddleGame[0][2] = tL;
        //[0][2] -> [1][2]
        this.twiddleGame[1][2] = tR;
        //[1][2] -> [1][1]
        this.twiddleGame[1][1] = bR;
        //[1][1] -> [0][1]
        this.twiddleGame[0][1] = bL;
    }

    public void rotateA() {
        int tL = this.twiddleGame[0][0];
        int tR = this.twiddleGame[0][1];
        int bL = this.twiddleGame[1][0];
        int bR = this.twiddleGame[1][1];
        //[0][0] -> [0][1]
        this.twiddleGame[0][1] = tL;
        //[0][1] -> [1][1]
        this.twiddleGame[1][1] = tR;
        //[1][1] -> [1][0]
        this.twiddleGame[1][0] = bR;
        //[1][0] -> [0][0]
        this.twiddleGame[0][0] = bL;
    }

    // inverted (CCW) rotations
    public void rotateDP() {
        int tL = this.twiddleGame[1][1];
        int tR = this.twiddleGame[1][2];
        int bL = this.twiddleGame[2][1];
        int bR = this.twiddleGame[2][2];
        //[1][1] <- [1][2]
        this.twiddleGame[1][1] = tR;
        //[1][2] <- [2][2]
        this.twiddleGame[1][2] = bR;
        //[2][2] <- [2][1]
        this.twiddleGame[2][2] = bL;
        //[2][1] <- [1][1]
        this.twiddleGame[2][1] = tL;
    }

    public void rotateCP() {
        int tL = this.twiddleGame[1][0];
        int tR = this.twiddleGame[1][1];
        int bL = this.twiddleGame[2][0];
        int bR = this.twiddleGame[2][1];
        //[1][0] <- [1][1]
        this.twiddleGame[1][0] = tR;
        //[1][1] <- [2][1]
        this.twiddleGame[1][1] = bR;
        //[2][1] <- [2][0]
        this.twiddleGame[2][1] = bL;
        //[2][0] <- [1][0]
        this.twiddleGame[2][0] = tL;
    }

    public void rotateBP() {
        int tL = this.twiddleGame[0][1];
        int tR = this.twiddleGame[0][2];
        int bL = this.twiddleGame[1][1];
        int bR = this.twiddleGame[1][2];
        //[0][1] <- [0][2]
        this.twiddleGame[0][1] = tR;
        //[0][2] <- [1][2]
        this.twiddleGame[0][2] = bR;
        //[1][2] <- [1][1]
        this.twiddleGame[1][2] = bL;
        //[1][1] <- [0][1]
        this.twiddleGame[1][1] = tL;
    }

    public void rotateAP() {
        int tL = this.twiddleGame[0][0];
        int tR = this.twiddleGame[0][1];
        int bL = this.twiddleGame[1][0];
        int bR = this.twiddleGame[1][1];
        //[0][0] <- [0][1]
        this.twiddleGame[0][0] = tR;
        //[0][1] <- [1][1]
        this.twiddleGame[0][1] = bR;
        //[1][1] <- [1][0]
        this.twiddleGame[1][1] = bL;
        //[1][0] <- [0][0]
        this.twiddleGame[1][0] = tL;
    }

    // ideal stage algorithm logics
    // in a perfect world, following this at each stage will get an efficient collection of moves
    // movement logic provided by https://codemyroad.wordpress.com/2015/04/13/solving-the-rotation-puzzle-in-stages/

    // Get 1 to [0][0]
    public Object[] stage1Logic() {

        // String to store our action as a String to be recorded later on
        String action;

        // possible circumstances
        if (twiddleGame[1][1] == 1 || twiddleGame[1][0] == 1) {
            this.rotateA();
            action = "A";
        } else if (twiddleGame[0][1] == 1) {
            this.rotateAP();
            action = "AP";
        } else if (twiddleGame[1][2] == 1) {
            this.rotateB();
            action = "B";
        } else if (twiddleGame[0][2] == 1) {
            this.rotateBP();
            action = "BP";
        } else if (twiddleGame[2][0] == 1 || twiddleGame[2][1] == 1) {
            this.rotateC();
            action = "C";
        } else {
            this.rotateD();
            action = "D";
        }

        // Action String paired with the number of moves expended
        // (since some steps in other logics require more than 1 move in some cases)
        return new Object[]{action, 1};
    }

    // Get 2 to [0][1]
    public Object[] stage2Logic() {

        String action;

        // possible circumstances
        if (this.twiddleGame[0][2] == 2) {
            this.rotateBP();
            action = "BP";
        } else if (this.twiddleGame[1][1] == 2 || this.twiddleGame[1][2] == 2) {
            this.rotateB();
            action = "B";
        } else if (this.twiddleGame[1][0] == 2 || this.twiddleGame[2][0] == 2) {
            this.rotateC();
            action = "C";
        } else {
            this.rotateD();
            action = "D";
        }
        return new Object[]{action, 1};
    }

    // Get 3 to [0][2]
    public Object[] stage3Logic() {

        String action = "";
        int numMoves = 0;

        // possible circumstances
        if (this.twiddleGame[1][0] == 3) {
            this.rotateC();
            action = "C";
            numMoves = 1;
        } else if (this.twiddleGame[2][0] == 3) {
            this.rotateCP();
            action = "CP";
            numMoves = 1;
        } else if (this.twiddleGame[1][1] == 3) {
            this.rotateDP();
            action = "DP";
            numMoves = 1;
        } else if (this.twiddleGame[1][2] == 3 || this.twiddleGame[2][2] == 3) {
            this.rotateD();
            action = "D";
            numMoves = 1;
        }
        // algorithm movement to get 3 where we want it from the correct location
        else if (this.twiddleGame[2][1] == 3) {
            this.rotateB();
            this.rotateB();
            this.rotateCP();
            this.rotateB();
            this.rotateB();
            action = "B,B,CP,B,B";
            numMoves = 5;
        }

        return new Object[]{action, numMoves};
    }

    // Get 7 to [2][0]
    public Object[] stage4Logic() {

        String action = "";

        // possible circumstances
        if (twiddleGame[1][1] == 7 || twiddleGame[2][1] == 7) {
            this.rotateC();
            action = "C";
        } else if (twiddleGame[1][0] == 7) {
            this.rotateCP();
            action = "CP";
        } else if (twiddleGame[1][2] == 7 || twiddleGame[2][2] == 7) {
            this.rotateD();
            action = "D";
        }

        return new Object[]{action, 1};
    }

    // Get 8 to [2][1]
    public Object[] stage5Logic() {

        String action = "";
        int numMoves = 0;

        // possible circumstances
        if (twiddleGame[1][2] == 8 || twiddleGame[2][2] == 8) {
            this.rotateD();
            action = "D";
            numMoves = 1;
        } else if (twiddleGame[1][1] == 8) {
            this.rotateDP();
            action = "DP";
            numMoves = 1;
        }
        // algorithm case
        else if (twiddleGame[1][0] == 8) {
            this.rotateC();
            this.rotateD();
            this.rotateCP();
            action = "C,D,CP";
            numMoves = 3;
        }
        return new Object[]{action, numMoves};
    }

    // Get 9 to [2][2]
    public Object[] stage6Logic() {

        // A really cheeky way of returning a String and an int from this method...
        String action = "";
        int numMoves = 0;

        // possible circumstances
        if (twiddleGame[1][0] == 9) {
            this.rotateC();
            this.rotateD();
            this.rotateD();
            this.rotateCP();
            action = "C,D,D,CP";
            numMoves = 4;
        } else if (twiddleGame[1][1] == 9) {
            this.rotateC();
            this.rotateDP();
            this.rotateCP();
            action = "C,DP,CP";
            numMoves = 3;
        } else if (twiddleGame[1][2] == 9) {
            this.rotateC();
            this.rotateD();
            this.rotateCP();
            action = "C,D,CP";
            numMoves = 3;
        }
        return new Object[]{action, numMoves};
    }

    // Get 5 to [1][1]
    public Object[] stage7Logic() {

        String action = "";

        // possible circumstances
        if (twiddleGame[1][0] == 5) {
            this.rotateC();
            this.rotateB();
            this.rotateCP();
            this.rotateBP();
            action = "C,B,CP,BP";
        } else if (twiddleGame[1][2] == 5) {
            this.rotateB();
            this.rotateC();
            this.rotateBP();
            this.rotateCP();
            action = "B,C,BP,CP";
        }
        return new Object[]{action, 4};
    }

    // Get 4 to [1][0]
    public Object[] stage8Logic() {

        String action = "";

        // possible circumstances
        if (twiddleGame[1][2] == 4) {
            this.rotateC();
            this.rotateC();
            this.rotateB();
            this.rotateC();
            this.rotateBP();
            this.rotateC();
            this.rotateB();
            this.rotateC();
            this.rotateBP();
            action = "C,C,B,C,BP,C,B,C,BP";
        }
        return new Object[]{action, 9};
    }

    // mess up move that goes against algorithm's intentions
    public Object[] messUpMove() {

        String action;

        int choice = ThreadLocalRandom.current().nextInt(0, 8);
        switch (choice) {
            case 0:
                this.rotateA();
                action = "A*";
                break;
            case 1:
                this.rotateB();
                action = "B*";
                break;
            case 2:
                this.rotateC();
                action = "C*";
                break;
            case 3:
                this.rotateD();
                action = "D*";
                break;
            case 4:
                this.rotateAP();
                action = "AP*";
                break;
            case 5:
                this.rotateBP();
                action = "BP*";
                break;
            case 6:
                this.rotateCP();
                action = "CP*";
                break;
            default:
                this.rotateDP();
                action = "DP*";
                break;
        }
        return new Object[]{action, 1};
    }
}
