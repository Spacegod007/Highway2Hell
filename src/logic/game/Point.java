package logic.game;

import java.io.Serializable;
import java.util.Objects;

/**
 * A 2 dimensional location
 */
public class Point implements Serializable
{
    /**
     * The location in the width
     */
    private double x;

    /**
     * The location in the height
     */
    private double y;

    /**
     * Constructs a location
     * @param x location in the width
     * @param y location in the height
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x location
     * @return the x location
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x location
     * @param x to be set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the y location
     * @return the y location
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y location
     * @param y to be set
     */
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return  "x=" + x +
                ", y=" + y;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj instanceof Point)
        {
            Point other = (Point) obj;

            return this.x == other.x && this.y == other.y;
        }

        return false;
    }
}
