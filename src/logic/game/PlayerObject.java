package logic.game;

import javafx.scene.paint.Color;

/**
 * A player in the game
 */
public class PlayerObject extends GameObject
{
    /**
     * The name of the player
     */
    private String name;

    /**
     * The color of the player
     */
    private String color;

    /**
     * The distance the player has traveled
     */
    private long distance;

    /**
     * If the player is dead
     */
    private boolean isDead = false;

    /**
     * The current rotation of the player
     */
    private double currentRotation;

    /**
     * Constructs a player object
     * @param anchor marks the location of the player
     * @param name of the player
     * @param color of the player
     */
    public PlayerObject(Point anchor, Size size, String name, Color color)
    {
        super(anchor, size);
        this.name = name;
        this.color = color.toString();
        this.currentRotation = 180d;
    }

    /**
     * Gets the distance the player has traveled
     * @return the distance the player has traveled
     */
    public long getDistance()
    {
        return distance;
    }

    /**
     * Sets the distance the player has traveled
     * @param distance to be set
     */
    public void setDistance(long distance)
    {
        this.distance = distance;
    }

    /**
     * Gets if the player is dead
     * @return true if the player is dead, false if the player is alive
     */
    public boolean getIsDead()
    {
        return isDead;
    }

    /**
     * Sets if the player is dead
     * @param dead true if the player is dead, false if the player is alive
     */
    public void setIsDead(boolean dead)
    {
        isDead = dead;
    }

    /**
     * Gets the current rotation of the player
     * @return the current rotation of the player
     */
    public double getCurrentRotation()
    {
        return currentRotation;
    }

    /**
     * Sets the current rotation of the player
     * @param currentRotation to be set
     */
    public void setCurrentRotation(double currentRotation)
    {
        this.currentRotation = currentRotation;
    }

    /**
     * Gets the current size of the player
     * @return an array containing the size of a player
     */
    public Size getPlayerSize()
    {
        return getSize();
    }

    /**
     * Sets the size of the player
     * @param playerSize to be set
     */
    public void setPlayerSize(Size playerSize)
    {
        setSize(playerSize);
    }

    /**
     * Gets the name of the player
     * @return the name of the player
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the player
     * @param name to be set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Gets the color of the player
     * @return the color of the player
     */
    public Color getColor()
    {
        return Color.valueOf(color);
    }

    /**
     * Sets the color of the player
     * @param color to be set
     */
    public void setColor(Color color)
    {
        this.color = color.toString();
    }

    /**
     * Moves the player in the specified direction
     * @param direction the player will move towards
     */
    public void move(Direction direction)
    {
        //You can't move if you're dead.
        if(!isDead)
        {
            //Changes the X and Y coordinates of the current player.
            switch(direction)
            {
                case LEFT:
                    double[] leftPoint = {this.getAnchor().getX()-16d, this.getAnchor().getY()-10d};
                    //Adds distance in pixels to score.
                    setDistance(getDistance() + (long)10d);

                    //Sets the current rotation
                    setCurrentRotation(170d);
                    System.out.println("rotation set");

                    this.setAnchor(new Point(leftPoint[0], leftPoint[1]));
                    return;

                case RIGHT:
                    double[] rightPoint = {getAnchor().getX()+16d, getAnchor().getY()-10d};
                    //Adds distance in pixels to score.
                    setDistance(getDistance() + (long)10d);

                    //Sets the current rotation
                    setCurrentRotation(190d);
                    System.out.println("rotation set");

                    this.setAnchor(new Point(rightPoint[0], rightPoint[1]));
                    break;
            }
        }
    }

    /**
     * Checks if there is a collision between a player object and an obstacle object
     * @param obstacleObject to check
     * @return true if there is a collision with the player object, false if there is no collision with the player object
     */
    public boolean checkForObstacleCollision(ObstacleObject obstacleObject)
    {
        //PO = PlayerObject
        //OO = ObstacleObject

        double POX = this.getAnchor().getX();
        double POY = this.getAnchor().getY();
        double POXWithWidth = POX + getSize().getWidth();
        double POYWithHeight = POY + getSize().getHeight();

        double OOX = obstacleObject.getAnchor().getX();
        double OOY = obstacleObject.getAnchor().getY();
        double OOXWithWidth = OOX + obstacleObject.getWidth();
        double OOYWithHeight = OOY + obstacleObject.getHeight();

        //Collision is not right yet. Need to check bottom right and top left corner of player as well.
        return POX >= OOX && POX <= OOXWithWidth && POY >= OOY && POY <= OOYWithHeight ||
                POXWithWidth >= OOX && POXWithWidth <= OOXWithWidth && POYWithHeight >= OOY && POYWithHeight <= OOYWithHeight;
    }
}
