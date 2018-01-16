package logic.game;

public enum CharacterColor
{
    black_blue("characters/character_black_blue.png"),
    black_red("characters/character_black_green.png"),
    black_white("characters/character_black_red.png"),
    black_green("characters/character_black_white.png"),
    blonde_blue("characters/character_blonde_blue.png"),
    blonde_green("characters/character_blonde_green.png"),
    blonde_red("characters/character_blonde_red.png"),
    blonde_white("characters/character_blonde_white.png"),
    brown_blue("characters/character_brown_blue.png"),
    brown_green("characters/character_brown_green.png"),
    brown_red("characters/character_brown_red.png"),
    brown_white("characters/character_brown_white.png");

    private final String path;

    public final String getPath()
    {
        return path;
    }


    CharacterColor(String path)
    {
        this.path = path;
    }
}