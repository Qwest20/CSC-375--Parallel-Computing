public class Station {

    // 0 = empty station, 1 = manga station, 2 = anime station
    private final int type;
    private boolean checked = false;

    // Constructor
    public Station(int t) {
        type = t;
    }

    // getters and setters
    public int getType() {
        return type;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
