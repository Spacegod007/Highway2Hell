package logic.administration;

import logic.game.CharacterColor;

import java.io.Serializable;
import java.util.Objects;

/**
 * A user/player who plays the game
 */
public class User implements Serializable
{
    /**
     * The username of the user
     */
    private final String username;

    /**
     * The id of the user
     */
    private final int id;

    /**
     * The lobby the user is currently located in
     */
    private Lobby activeLobby;

    /**
     * The color of the in-game character of the current user
     */
    private CharacterColor characterColor;

    /**
     * Gets the id of the user
     * @return the id of the user
     */
    public int getId()
    {
        return id;
    }

    /**
     * Gets the active lobby of the user
     * @return the lobby the user is currently located in
     */
    public Lobby getActiveLobby()
    {
        return activeLobby;
    }

    /**
     * Sets the active lobby of the user
     * @param activeLobby to be set
     */
    public void setActiveLobby(Lobby activeLobby)
    {
        this.activeLobby = activeLobby;
    }

    /**
     * Gets the username of the user
     * @return the username of the user
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Constructs an user object
     * @param username of the user
     * @param id of the user
     */
    public User(String username, int id) {
        this.username = username;
        this.id = id;
        this.characterColor = CharacterColor.BLACK_BLUE;
    }

    /**
     * Gets the character color of the user
     * @return a CharacterColor value of the enumeration
     */
    public CharacterColor getCharacterColor()
    {
        return characterColor;
    }

    /**
     * Sets the character color of the user
     * @param characterColor to be set
     */
    public void setCharacterColor(CharacterColor characterColor)
    {
        this.characterColor = characterColor;
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof User)
        {
            User other = (User) obj;

            return id == other.id;
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
