package mpc.utexas.edu.warble2.features;

/**
 * Created by yosef on 1/27/2018.
 */

public class Location {
    public static String TAG = "Location";
    public static String identifier = "Location";
    protected int xCoordinate;
    protected int yCoordinate;

    public Location(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public int getxCoordinate() {
        return this.xCoordinate;
    }

    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getyCoordinate() {
        return this.yCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}
