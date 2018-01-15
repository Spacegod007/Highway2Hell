package logic;

/**
 * A game rule in the game
 */
public class Gamerule
{
    /**
     * The selected game rule in the enumeration
     */
    private final Gamerules gamerules;

    /**
     * Gets the selected game rule
     * @return the selected game rule
     */
    public Gamerules getGamerules()
    {
        return gamerules;
    }

    /**
     * Gets the value of the game rule
     * @return the value of the game rule
     */
    public int getValue()
    {
        return value;
    }

    /**
     * Contains the value of the game rule
     */
    private int value;

    /**
     * Constructs a game rule object
     * @param gamerule to be selected
     */
    public Gamerule(Gamerules gamerule)
    {
        this.gamerules = gamerule;
        this.value = 0;
    }

    /**
     * Sets the value of the game rule
     * @param value to be set
     */
    public void setValue(int value)
    {
        this.value = value;
    }
}
