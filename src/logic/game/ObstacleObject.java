package logic.game;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Marks an object as an obstacle in the game
 */
public class ObstacleObject extends GameObject
{
    private static AtomicInteger nextId = new AtomicInteger(0);
    private int id;
    public int getId(){return id;}
    private ObstacleType type;

    /**
     * Constructs a new obstacle object
     *
     */
    public ObstacleObject()
    {
        super(new Point(ThreadLocalRandom.current().nextInt(0, 1150  + 1), ThreadLocalRandom.current().nextInt(-500, 1)), new Size(1, 1));
        generateRandomObjectType();
        this.id = nextId.getAndIncrement();
    }

    /**
     * Gets the width of the object
     * @return the width of the object
     */
    public double getWidth()
    {
        return getSize().getWidth();
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
    public double getHeight()
    {
        return getSize().getHeight();
    }

    /**
     * Sets the height of the object
     * @param height to be set
     */
    public void setHeight(int height)
    {
        getSize().setHeight(height);
    }

    public ObstacleType getType()
    {
        return type;
    }

    private void generateRandomObjectType()
    {
        Random random = new Random(System.currentTimeMillis());
        int randomNumber = random.nextInt(3);
        switch (randomNumber)
        {
            case 0:
                type = ObstacleType.RED_BARREL;
                setWidth(70);
                setHeight(48);
                break;
            case 1:
                type = ObstacleType.BLUE_BARREL;
                setWidth(70);
                setHeight(48);
                break;
            case 2:
                type = ObstacleType.ROCK;
                setWidth(89);
                setHeight(72);
                break;
            default:
                break;
        }
    }

    public enum ObstacleType
    {
        RED_BARREL,
        BLUE_BARREL,
        ROCK
    }
}
