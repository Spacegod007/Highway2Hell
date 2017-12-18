package logic.game;

import java.io.Serializable;

/**
 * A object located in the game
 */
public abstract class GameObject implements Serializable
{
    /**
     * the position of the game object
     */
    private Point anchor;

    /**
     * The size of the game object
     */
    private Size size;

    /**
     * Constructs an object in the game
     * @param anchor the position of the object
     */
    public GameObject(Point anchor, Size size) {
        this.anchor = anchor;
        this.size = size;
    }

    /**
     * Gets the location of the game object
     * @return the location of the game object
     */
    public Point getAnchor() {
        return anchor;
    }

    /**
     * Sets the location of the game object
     * @param anchor to be set
     */
    public void setAnchor(Point anchor) {
        this.anchor = anchor;
    }

    /**
     * Gets the size of the game object
     * @return the size of the game object
     */
    public Size getSize()
    {
        return size;
    }

    /**
     * Sets the size of the game object
     * @param size to be set
     */
    public void setSize(Size size)
    {
        this.size = size;
    }
}
