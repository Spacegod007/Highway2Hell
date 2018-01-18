package logic.game;

public enum CharacterColor
{
    /**
     * Character with black hair and blue body
     */
    black_blue("characters/character_black_blue.png"),

    /**
     * Character with black hair and red body
     */
    black_red("characters/character_black_green.png"),

    /**
     * Character with black hair and white body
     */
    black_white("characters/character_black_red.png"),

    /**
     * Character with black hair and green body
     */
    black_green("characters/character_black_white.png"),

    /**
     * Character with blond hair and blue body
     */
    blonde_blue("characters/character_blonde_blue.png"),

    /**
     * Character with blond hair and green body
     */
    blonde_green("characters/character_blonde_green.png"),

    /**
     * Character with blond hair and red body
     */
    blonde_red("characters/character_blonde_red.png"),

    /**
     * Character with blond hair and white body
     */
    blonde_white("characters/character_blonde_white.png"),

    /**
     * Character with brown hair and blue body
     */
    brown_blue("characters/character_brown_blue.png"),

    /**
     * Character with brown hair and green body
     */
    brown_green("characters/character_brown_green.png"),

    /**
     * Character with brown hair and red body
     */
    brown_red("characters/character_brown_red.png"),

    /**
     * Character with brown hair and white body
     */
    brown_white("characters/character_brown_white.png");

    /**
     * The location of the player image linked to the color
     */
    private final String path;

    /**
     * Gets the path of the player image
     * @return
     */
    public final String getPath()
    {
        return path;
    }


    /**
     * Constructs an CharacterColor object
     * @param path to the character file
     */
    CharacterColor(String path)
    {
        this.path = path;
    }
}