import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TwiddleDisplayGUI extends JFrame {

    // private variables for the GUI object
    private final int width;
    private final int height;

    // references
    private final int[][] twiddleStart;
    private final String winner;
    private final ArrayList<String> winningMoves;
    private final int numMoves;
    private final int numThreads;

    // Constructor
    public TwiddleDisplayGUI(int w, int h, int[][] tS, String wi, ArrayList<String> wM, int nM, int nT){
        this.width = w;
        this.height = h;
        this.twiddleStart = tS;
        this.winner = wi;
        this.winningMoves = wM;
        this.numMoves = nM;
        this.numThreads = nT;
    }

    // Paint method to visualize the wholeRoom 2D array of stations
    public void paint(Graphics g) {

        // headers
        g.setColor(Color.black);
        g.setFont(new Font("Default", Font.BOLD, 25));
        g.drawString("Starting State:", 20,50);
        g.setFont(new Font("Default", Font.BOLD, 25));
        g.drawString("Solving Moves (according to "+winner+"):", ((width*2)/5)-50,50);

        // starting state on the left-hand side
        for(int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++) {

                // creates a 1x1 square in the proper location
                g.setColor(Color.lightGray);
                int squareBuffers = 20;
                int hBuffer = (j+2)* squareBuffers;
                int vBuffer = (i+2)* squareBuffers;
                int shapeScalar = 3;
                int xSpot = ((this.width/3)/9)*j* shapeScalar + hBuffer;
                int topBufferPortion = 50;
                int ySpot = topBufferPortion + ((this.height/3)/9)*i* shapeScalar + vBuffer;
                int tempW = ((this.width/3)/9)* shapeScalar;
                int tempH = ((this.height/3)/9)* shapeScalar;
                g.fillRect(xSpot, ySpot, tempW, tempH);

                // add the number as text in the proper location
                g.setColor(Color.black);
                g.setFont(new Font("Default", Font.BOLD, 25));
                g.drawString(String.valueOf(twiddleStart[i][j]),xSpot + tempW/2, ySpot + tempH/2);
            }
        }

        // List of moves on the right-hand side
        for (int i = 0; i < winningMoves.size(); i++) {
            String move = winningMoves.get(i);
            g.setColor(Color.black);
            g.setFont(new Font("Default", Font.PLAIN, 20));
            if(i<25)
                g.drawString((i+1)+") "+move,width/2 + 50, 100+(30*i));
            else if (i<50)
                g.drawString((i+1)+") "+move,(width/2) + 150, 100+(30*(i-25)));
            else if (i<75)
                g.drawString((i+1)+") "+move,(width/2) + 250, 100+(30*(i-50)));
            else
                g.drawString((i+1)+") "+move,(width/2) + 350, 100+(30*(i-75)));
        }

        // Text describing the GUI data at bottom left (a footnote sort of dealio)
        g.setColor(Color.black);
        g.setFont(new Font("Default", Font.BOLD, 25));
        g.drawString("KEY:", 50,(height/2)+50);
        g.setFont(new Font("Default", Font.PLAIN, 20));
        g.drawString("A = 4 top left blocks rotated CW", 60,(height/2)+100);
        g.drawString("B = 4 top right blocks rotated CW", 60,(height/2)+130);
        g.drawString("C = 4 bottom left blocks rotated CW", 60,(height/2)+160);
        g.drawString("D = 4 bottom right blocks rotated CW", 60,(height/2)+190);
        g.drawString("XP = quadrant X rotated CCW", 60,(height/2)+220);
        g.drawString("X* or XP* = \"Mess up move\"", 60,(height/2)+250);
        g.drawString("(Mess ups occur when the thread", 60,(height/2)+300);
        g.drawString("is forced to deviate from the", 60,(height/2)+330);
        g.drawString("general solving algorithm)", 60,(height/2)+360);

        // Depth Footnote
        g.drawString("DEPTH: <= "+numMoves+" moves", 60,height - 50);
        // Parallelism Footnote
        g.drawString("PARALLELISM: "+numThreads+" threads", 60,height - 25);
    }

    // Make the GUI
    public void setUpGUI(){
        // initial settings
        this.setVisible(true);
        this.setSize(width,height);
        this.setTitle("Twiddle Output");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
