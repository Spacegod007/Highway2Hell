package logic.game;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Marks an object as an obstacle in the game
 */
public class ObstacleObject extends GameObject
{
    /**
     * Constructs a new obstacle object
     * @param width of the object
     * @param height of the object
     */
    public ObstacleObject(int width, int height)
    {
        super(new Point(ThreadLocalRandom.current().nextInt(0, 1150  + 1), ThreadLocalRandom.current().nextInt(-500, 1)), new Size(width, height));
    }

    /**
     * Gets the width of the object
     * @return the width of the object
     */
    public int getWidth()
    {
        return (int) getSize().getWidth();
    }

    /**
     * Sets the width of the object
     * @param width to be set
     */
    public void setWidth(int width)
    {
        getSize().setWidth(width);
    }

    /**
     * Gets the height of the object
     * @return the height of the object
     */
    public int getHeight()
    {
        return (int) getSize().getHeight();
    }

    /**
     * Sets the height of the object
     * @param height to be set
     */
    public void setHeight(int height)
    {
        getSize().setHeight(height);
    }
}
