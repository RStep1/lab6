package data;

/**
 * Contains two coordinate variables for Vehicle class.
 */
public class Coordinates {
    private float x;
    private double y;
    private static final int accuracy = 5;

    public Coordinates(float x, double y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public static int getAccuracy() {
        return accuracy;
    }

    @Override
    public String toString() {
        return String.format("(%." + accuracy + "f; %." + accuracy + "f)", x, y);
    }

}
