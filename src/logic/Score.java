package logic;

/**
 * A score object
 */
public class Score
{
    /**
     * The name of the player who has the score
     */
    private String name;

    /**
     * The value of the score the player has
     */
    private double value;

    /**
     * Constructs the score object
     * @param name of the player
     * @param score of the player
     */
    public Score(String name, double score)
    {
        this.name = name;
        this.value = score;
    }

    /**
     * Gets the name of the player
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the player
     * @param name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the score of the player
     * @return the score of the player
     */
    public double getScore() {
        return value;
    }

    /**
     * Sets the score of the player
     * @param score to be set
     */
    public void setScore(double score) {
        this.value = score;
    }
}
