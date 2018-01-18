package logic.game;

@SuppressWarnings("SpellCheckingInspection")
public enum CharacterColor
{
    /**
     * Character with black hair and blue body
     */
    BLACK_BLUE("characters/character_black_blue.png"),

    /**
     * Character with black hair and red body
     */
    BLACK_RED("characters/character_black_green.png"),

    /**
     * Character with black hair and white body
     */
    BLACK_WHITE("characters/character_black_red.png"),

    /**
     * Character with black hair and green body
     */
    BLACK_GREEN("characters/character_black_white.png"),

    /**
     * Character with blond hair and blue body
     */
    BLONDE_BLUE("characters/character_blonde_blue.png"),

    /**
     * Character with blond hair and green body
     */
    BLONDE_GREEN("characters/character_blonde_green.png"),

    /**
     * Character with blond hair and red body
     */
    BLONDE_RED("characters/character_blonde_red.png"),

    /**
     * Character with blond hair and white body
     */
    BLONDE_WHITE("characters/character_blonde_white.png"),

    /**
     * Character with brown hair and blue body
     */
    BROWN_BLUE("characters/character_brown_blue.png"),

    /**
     * Character with brown hair and green body
     */
    BROWN_GREEN("characters/character_brown_green.png"),

    /**
     * Character with brown hair and red body
     */
    BROWN_RED("characters/character_brown_red.png"),

    /**
     * Character with brown hair and white body
     */
    BROWN_WHITE("characters/character_brown_white.png");

    /**
     * The location of the player image linked to the color
     */
    private final String path;

    /**
     * Gets the path of the player image
     * @return a string object containing the path to the image
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