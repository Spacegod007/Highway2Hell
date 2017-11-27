package logic.game;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Marks an object as an obstacle in the game
 */
public class ObstacleObject extends GameObject
{
    //TODO change the width and height to a size object in the gameobject class, Size class has already been made -Jordi

    /**
     * The Width of an object
     */
    private int width;

    /**
     * The height of an object
     */
    private int height;

    /**
     * Constructs a new obstacle object
     * @param width of the object
     * @param height of the object
     */
    public ObstacleObject(int width, int height)
    {
        super(new Point(ThreadLocalRandom.current().nextInt(0, 1150  + 1), ThreadLocalRandom.current().nextInt(-500, 1)));
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the width of the object
     * @return the width of the object
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Sets the width of the object
     * @param width to be set
     */
    public void setWidth(int width)
    {
        this.width = width;
    }

    /**
     * Gets the height of the object
     * @return the height of the object
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Sets the height of the object
     * @param height to be set
     */
    public void setHeight(int height)
    {
        this.height = height;
    }
}
