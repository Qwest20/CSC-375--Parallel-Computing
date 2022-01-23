import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class HalfRoom {

    // A Halfroom contains half as many station spots as the whole room does (32 instead of 64)
    private Station[][] Stations;
    private int affinity = 0;

    // fill the half room with 20 non-empty stations and 12 empty stations
    // this will make for a total combination of 40 filled stations in 64 possible spots
    public HalfRoom() {

        // create a halfroom of stations
        Stations = new Station[4][8];

        // randomly decide on the anime and manga station frequencies (numManga+numAnime = 20 ALWAYS)
        // NOTE: To keep things somewhat streamlined, there can be anywhere between 8 and 12 manga rooms
        int numMangaStations = ThreadLocalRandom.current().nextInt(8, 12 + 1);
        int numAnimeStations = 20 - numMangaStations;

        // iterate through the half room to fill all station spots with some kind of station
        for (int i = 0; i < Stations.length; i++) {
            for (int j = 0; j < Stations[i].length; j++) {
                // make a manga station
                if (numMangaStations > 0) {
                    Stations[i][j] = new Station(1);
                    numMangaStations--;
                }
                // make an anime station
                else if (numAnimeStations > 0) {
                    Stations[i][j] = new Station(2);
                    numAnimeStations--;
                }
                // make an empty station
                else {
                    Stations[i][j] = new Station(0);
                }
            }
        }

        // shuffle the stations around in here
        Random random = new Random();
        for (int i = 3 ; i >= 0; i--) {
            for (int j = 7 ; j >= 0; j--) {
                int m = random.nextInt(i + 1);
                int n = random.nextInt(j + 1);

                Station temp = Stations[i][j];
                Stations[i][j] = Stations[m][n];
                Stations[m][n] = temp;
            }
        }

        // Calculate affinity for the first time
        calculateAffinity();
    }

    // Getters and Setters

    public int getAffinity() {
        return affinity;
    }

    public Station[][] getStations() {
        return Stations;
    }

    public void setStations(Station[][] stations) {
        Stations = stations;
    }

    // Calculate Affinity Method

    public void calculateAffinity() {

        // Affinity Logic:
        // In this program, we will be coordinating the placement of stations that produce manga (Japanese comics) and anime (Japanese cartoons)
        // Manga stations (represented in orange) like to be close to other stations, no matter what flavor they may be.
        // Anime stations (represented in blue) like to be close to manga stations, but away from other anime stations.
        // The distance between a manga station and any other station type subtracts 1 affinity for each unit if the distance is greater than 1 unit
        // The distance between an anime station and any other anime station adds 1 affinity for each unit if the distance is greater than 1 unit
        // NOTE: no station is ever counted twice. Once a station is checked with respect to the other stations, it will not be checked as the others are
        // For example, if we observe the affinities of the other stations from the station at (1,4), we will exclude checking the station at (1,4) when we check from other stations in the room later on.

        // Some extra background on why this system is structured in this manner.
        // (Manga to Manga closer proximity) This allows the authors that write the manga in the manga stations to collaborate with other manga authors in other manga stations to find new inspiration for their work! Creativity is fostered in a cooperative environment after all!
        // (Manga to Anime closer proximity) This allows the authors in the manga stations to network with anime stations to get their manga adapted into an anime release. This is easier when anime stations are nearby of course.
        // (Anime to Anime further proximity) This is because anime stations will prefer to get their work done completely in house, and don't usually rely on other anime stations for assistance (as far as I can tell at least...)

        // starts off at the base value and subtracts accordingly
        this.affinity = 0;
        // calculate affinity of this half room
        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 8 ; j++) {
                if (!Stations[i][j].isChecked()) {
                    // observe the current station that we're on and begin scanning through the 2D array again with reference to this particular one
                    int typeInQuestion = Stations[i][j].getType();
                    for (int k = 0; k < 4 ; k++) {
                        for (int l = 0; l < 8 ; l++) {
                            // we're looking at a station that isn't ours that hasn't been checked already
                            if (!(k == i && l == j) && !Stations[k][l].isChecked()) {
                                switch (typeInQuestion) {
                                    // we're looking at a manga station
                                    case 1:
                                        // if a non-empty station, manga station calculates accordingly. Manga likes to be near everything!
                                        if (Stations[k][l].getType() == 1 || Stations[k][l].getType() == 2) {
                                            int score = Math.abs(k - i) - 1 + Math.abs(j - l) - 1;
                                            this.affinity -= score;
                                        }
                                        break;
                                    // we're looking at an anime station
                                    case 2:
                                        // if a non-empty station, anime station calculates accordingly. Anime likes to be near manga!
                                        if (Stations[k][l].getType() == 1) {
                                            int score = Math.abs(k - i) - 1 + Math.abs(j - l) - 1;
                                            this.affinity -= score;
                                        }
                                        // if a non-empty station, anime station calculates accordingly. Anime likes to be further from anime!
                                        if (Stations[k][l].getType() == 2) {
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
                    Stations[i][j].setChecked(true);
                }
            }
        }
        // reset the checked values for all the stations again
        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 8 ; j++) {
                Stations[i][j].setChecked(false);
            }
        }
    }
}
