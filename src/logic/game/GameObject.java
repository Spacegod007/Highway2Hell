package logic.game;

/**
 * A object located in the game
 */
public abstract class GameObject
{
    /**
     * the position of the game object
     */
    private Point anchor;

    /**
     * Constructs an object in the game
     * @param anchor the position of the object
     */
    public GameObject(Point anchor) {
        this.anchor = anchor;
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
}
