import java.util.concurrent.ThreadLocalRandom;

public class Room {

    private HalfRoom halfA = new HalfRoom();
    private HalfRoom halfB = new HalfRoom();
    private final Station[][] wholeRoom = new Station[8][8];
    private int affinity = 0;

    // Constructor
    public Room(){
        // update this room's data based on its created halfrooms
        this.refreshWholeRoom();
        // update the starting affinity based on the half rooms that we have
        this.updateAffinity();
    }

    // Getters and Setters
    public HalfRoom getHalfA() {
        return halfA;
    }

    public HalfRoom getHalfB() {
        return halfB;
    }

    public void setHalfA(HalfRoom halfA) {
        this.halfA = halfA;
    }

    public void setHalfB(HalfRoom halfB) {
        this.halfB = halfB;
    }

    public int getAffinity() {
        return affinity;
    }

    public Station[][] getWholeRoom() {
        return wholeRoom;
    }

    // swapping methods
    private void swapLeft(int i, int j) {
        // swap left
        if (j > 0 && wholeRoom[i][j - 1] != null) {
            Station temp = wholeRoom[i][j];
            wholeRoom[i][j] = wholeRoom[i][j - 1];
            wholeRoom[i][j - 1] = temp;
        }
    }

    private void swapRight(int i, int j) {
        // swap right
        if (j < 7 && wholeRoom[i][j + 1] != null) {
            Station temp = wholeRoom[i][j];
            wholeRoom[i][j] = wholeRoom[i][j + 1];
            wholeRoom[i][j + 1] = temp;
        }
    }

    private void swapUp(int i, int j) {
        // swap up
        if (i > 0 && wholeRoom[i - 1][j] != null) {
            Station temp = wholeRoom[i][j];
            wholeRoom[i][j] = wholeRoom[i - 1][j];
            wholeRoom[i - 1][j] = temp;
        }
    }

    private void swapDown(int i, int j) {
        // swap down
        if (i < 7 && wholeRoom[i + 1][j] != null) {
            Station temp = wholeRoom[i][j];
            wholeRoom[i][j] = wholeRoom[i + 1][j];
            wholeRoom[i + 1][j] = temp;
        }
    }

    // Private methods
    // Just like the half room but for this ENTIRE room! This will be different,
    // since now we can calculate affinity on stations that can be 8 units apart height wise instead of the original 4 units in the half rooms
    private void updateAffinity() {

        // starts off at the base value and subtracts accordingly
        this.affinity = 0;
        // calculate affinity of this room
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!wholeRoom[i][j].isChecked()) {
                    int typeInQuestion = wholeRoom[i][j].getType();
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            // we're looking at a station that isn't ours that hasn't been checked already
                            if (!(k == i && l == j) && !wholeRoom[k][l].isChecked()) {
                                switch (typeInQuestion) {
                                    // we're looking at a manga station
                                    case 1:
                                        // if a non-empty station, manga station calculates accordingly. Manga likes to be near everything!
                                        if (wholeRoom[k][l].getType() == 1 || wholeRoom[k][l].getType() == 2) {
                                            int score = Math.abs(k - i) - 1 + Math.abs(j - l) - 1;
                                            this.affinity -= score;
                                        }
                                        break;
                                    // we're looking at an anime station
                                    case 2:
                                        // if a non-empty station, manga station calculates accordingly. Anime likes to be near manga!
                                        if (wholeRoom[k][l].getType() == 1) {
                                            int score = Math.abs(k - i) - 1 + Math.abs(j - l) - 1;
                                            this.affinity -= score;
                                        }
                                        // if a non-empty station, anime station calculates accordingly. Anime likes to be further from anime!
                                        if (wholeRoom[k][l].getType() == 2) {
                                            int score = Math.abs(k - i) - 1 + Math.abs(j - l) - 1;
                                            this.affinity += score;
                                        }
                                        break;
                                    // empty station, no logic needed
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                    // anything involving this station will not be considered anymore.
                    // in other words, no distance scores are considered twice!
                    wholeRoom[i][j].setChecked(true);
                }
            }
        }
        // reset the checked values for all the wholeRoom again
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                wholeRoom[i][j].setChecked(false);
            }
        }
    }

    // Public methods

    // Refresh the room's stations based on the attached half rooms
    // We will call this AFTER ROOM SWAPPING!
    public void refreshWholeRoom() {
        // refresh the whole room
        for (int k = 0; k < 4; k++) {
            for (int l = 0; l < 8; l++) {
                wholeRoom[k][l] = halfA.getStations()[k][l];
            }
        }
        for (int m = 0; m < 4; m++) {
            for (int o = 0; o < 8; o++) {
                wholeRoom[m+4][o] = halfB.getStations()[m][o];
            }
        }

        // update the affinity of this room since we made some changes to it
        updateAffinity();
    }

    // station movement logic
    public void optimizeRoom() {

        // Within each room, apply the following logic to swap the stations for the most beneficial outcome. The logic to do so is explained here:
        // Try to get anime stations to the perimeter or center of the room. The less clustered they are, the better
        // the greater the distance, the greater the points, and they won't subtract points if they're close.
        // Try to fixate the manga stations into a cluster somewhere in the room. It won't matter where this cluster ends up,
        // since anywhere will allow manga stations to keep their affinity points so long as they're huddled near other stations

        //refresh this wholeRoom, so we are up-to-date on the half rooms to look at
        refreshWholeRoom();

        // iterate through the stations once each time to see if they can be swapped
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // look at wholeRoom[i][j]
                int typeInQuestion = wholeRoom[i][j].getType();
                switch (typeInQuestion) {
                    // we're looking at a manga station
                    case 1:
                        // get 1's to the center
                        // if this manga station is not in the center
                        if (!(i == 3 || i == 4 || j == 3 || j == 4)) {
                            // vertical swap would be more beneficial than a horizontal swap roughly, and it randomly revolves around the middle area
                            if (Math.abs(i - ThreadLocalRandom.current().nextInt(3, 4 + 1)) >= Math.abs(j - ThreadLocalRandom.current().nextInt(3, 4 + 1))) {
                                // can improve vertically
                                if (i < 3) {
                                    swapDown(i,j);
                                }
                                if (i > 4) {
                                    swapUp(i,j);
                                }
                            } else {
                                // can improve horizontally
                                if (j < 3) {
                                    swapRight(i,j);
                                }
                                if (j > 4) {
                                    swapLeft(i,j);
                                }
                            }
                        }
                        break;
                    // we're looking at an anime station
                    case 2:
                        // get 2's to the edges
                        // if this anime station is not on the perimeter
                        if (!(i == 0 || i == 7 || j == 0 || j == 7)) {
                            // horizontal swap would be more beneficial than a vertical swap roughly
                            if (Math.abs(i - 3) > Math.abs(j - 3)) {
                                // can improve horizontally
                                if (j < 3) {
                                    swapLeft(i,j);
                                }
                                if (j > 4) {
                                    swapRight(i,j);
                                }
                            }
                            else{
                                // can improve vertically
                                if (i < 3) {
                                    swapUp(i,j);
                                }
                                if (i > 4) {
                                    swapDown(i,j);
                                }
                            }
                        }
                        // if we're on the perimeter, aim for the corners. These will essentially swap like a clockwise swimming pool whirlpool on the edge! :D
                        else{
                            if(i==0){
                                swapLeft(i,j);
                            }
                            if(j==0){
                                swapDown(i,j);
                            }
                            if(i==7){
                                swapRight(i,j);
                            }
                            if(j==7){
                                swapUp(i,j);
                            }
                        }
                        break;
                    // we're looking at an empty station
                    default:
                        break;
                }
            }
        }
        // update the internal affinity value based on the newly optimized rooms
        updateAffinity();

        // refresh the half rooms with this new data
        Station[][] newHalfA = new Station[4][8];
        Station[][] newHalfB = new Station[4][8];
        for (int k = 0; k < 4; k++) {
            System.arraycopy(wholeRoom[k], 0, newHalfA[k], 0, 8);
        }
        for (int m = 0; m < 4; m++) {
            System.arraycopy(wholeRoom[m + 4], 0, newHalfB[m], 0, 8);
        }
        halfA.setStations(newHalfA);
        halfB.setStations(newHalfB);
        // update the affinity of these halfrooms here
        halfA.calculateAffinity();
        halfB.calculateAffinity();
    }
}
