package logic;

/**
 * A gamerule in the game
 */
public class Gamerule
{
    /**
     * The selected gamerule in the enumeration
     */
    private Gamerules gamerules;

    /**
     * Gets the selected gamerule
     * @return the selected gamerule
     */
    public Gamerules getGamerules()
    {
        return gamerules;
    }

    /**
     * Gets the value of the gamerule
     * @return the value of the gamerule
     */
    public int getValue()
    {
        return value;
    }

    /**
     * Contains the value of the gamerule
     */
    private int value;

    /**
     * Constructs a gamerule object
     * @param gamerule to be selected
     */
    public Gamerule(Gamerules gamerule)
    {
        this.gamerules = gamerule;
        this.value = 0;
    }

    /**
     * Sets the value of the gamerule
     * @param value to be set
     */
    public void setValue(int value)
    {
        this.value = value;
    }
}
