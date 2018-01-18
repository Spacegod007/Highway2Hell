package logic.game;

import java.io.Serializable;

/**
 * the 2 dimensional size of objects
 */
public class Size implements Serializable
{
    /**
     * The width of the object
     */
    private double width;

    /**
     * The height of the object
     */
    private double height;

    /**
     * A size object, the size of an object.
     * @param width must be more than 0
     * @param height must be more than 0
     * @throws IndexOutOfBoundsException width and height need to be more than 0
     */
    public Size(double width, double height)
    {
        if (width <= 0 || height <= 0)
        {
            throw new IndexOutOfBoundsException("width nor height of object can be less or equal to 0.");
        }

        this.width = width;
        this.height = height;
    }

    /**
     * gets the width of an object.
     * @return the width of an object.
     */
    public double getWidth()
    {
        return width;
    }

    /**
     * sets the width of an object.
     * @param width the width of an object, must be more than 0.
     */
    public void setWidth(double width)
    {
        if (width <= 0)
            throw new IndexOutOfBoundsException("Width of object cannot be less or equal to 0.");

        this.width = width;
    }

    /**
     * gets the height of an object.
     * @return the height of an object.
     */
    public double getHeight()
    {
        return height;
    }

    /**
     * sets the height of an object.
     * @param height the height of an object, must be more than 0.
     */
    public void setHeight(double height)
    {
        if (height <= 0)
            throw new IndexOutOfBoundsException("Height of object cannot be less or equal to 0.");
        
        this.height = height;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj instanceof Size)
        {
            Size other = (Size) obj;

            return this.width == other.width && this.height == other.height;
        }

        return false;
    }
}
