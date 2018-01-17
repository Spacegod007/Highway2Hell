package logic.administration;

import logic.game.CharacterColor;

import java.io.Serializable;

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
    private final int ID;

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
    public int getID()
    {
        return ID;
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
     * @param ID of the user
     */
    public User(String username, int ID) {
        this.username = username;
        this.ID = ID;
        this.characterColor = CharacterColor.black_blue;
    }

    public CharacterColor getCharacterColor()
    {
        return characterColor;
    }

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

            return ID == other.ID;
        }

        return false;
    }
}
