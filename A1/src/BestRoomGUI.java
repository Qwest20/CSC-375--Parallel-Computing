import java.awt.*;
import javax.swing.*;

public class BestRoomGUI extends JFrame{

    // private variables for the GUI object, including a reference to the room being displayed and the iteration it's on
    private int width;
    private int height;
    private Room bestRoom;
    private int iteration;

    // Constructor
    public BestRoomGUI(int w, int h, Room r, int i){
        this.width = w;
        this.height = h;
        this.bestRoom = r;
        this.iteration = i;
    }

    // Paint method to visualize the wholeRoom 2D array of stations
    public void paint(Graphics g) {
        for(int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++) {
                // manga station
                if(this.bestRoom.getWholeRoom()[i][j].getType() == 1){
                    g.setColor(Color.orange);
                }
                // anime station
                else if(this.bestRoom.getWholeRoom()[i][j].getType() == 2){
                    g.setColor(Color.blue);
                }
                // empty station
                else{
                    g.setColor(Color.white);
                }
                // creates a 1x1 rectangle in the proper location in the 8x8 grid space
                g.fillRect((this.width/8)*j, (this.height/8)*i, this.width/8, this.height/8);
            }
        }
    }

    // Make the GUI
    public void setUpGUI(){
        this.setVisible(true);
        this.setSize(width,height);
        this.setTitle("Best Room in Room Collection at iteration "+this.iteration);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
}
