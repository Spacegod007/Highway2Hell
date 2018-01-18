package logic.administration;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A lobby where users which users will be able to join before the game started
 */
public class Lobby implements Serializable
{
    private static final Logger LOGGER = Logger.getLogger(Lobby.class.getName());
    /**
     * The id of the lobby
     */
    private final int id;

    /**
     * The host of the lobby
     */
    private User host;

    /**
     * The name of the lobby
     */
    private final String name;

    /**
     * The players that are currently in the lobby
     */
    private final List<User> players;

    /**
     * The ipAddress used to connect to the server when the game starts
     */
    private final String ipAddress;

    /**
     * The maximum amount of players allowed
     */
    private static final int MAX_SIZE = 64; // static?

    /**
     * Gets the host of this lobby
     * @return an User object containing the host of this lobby
     */
    public User getHost()
    {
        return host;
    }

    /**
     * Gets the id of this lobby
     * @return the id of this lobby
     */
    public int getId()
    {
        return id;
    }

    public void setHost(User host)
    {
        this.host = host;
    }

     /**
     * Gets a list of all players in this lobby
     * @return a list of users containing individual players
     */
    public ObservableList<User> getPlayers()
    {
        return FXCollections.unmodifiableObservableList(FXCollections.observableList(players));
    }

    /**
     * Gets the name of this lobby
     * @return the name of the lobby
     */
    public String getName()
    {
        return name;
    }

    /**
     * Constructor of the lobby
     * @param name of the lobby
     * @param id of the lobby
     * @param ipAddress of the host
     */
    public Lobby(String name, int id, String ipAddress)
    {
        players = new ArrayList<>();
        /*
      The game rules bound to this lobby by the host
     */
        this.name = name;
        this.id = id;
        this.ipAddress = ipAddress;
    }

    /**
     * Removes the specified userId from the lobby
     * @param userId of the user who will be removed from the lobby
     * @return true if the user was removed, false if the user was not removed
     */
    public void leave(int userId)
    {
        for(User p : players){
            if(p.getId() == userId)
            {
                this.players.remove(p);
                return;
            }
        }
    }

    /**
     * Adds a player to the lobby
     * @param player to be added to this lobby
     * @return true if the player was added, false if the adding of the player failed
     */
    public boolean join(User player)
    {
        try
        {
            if (host == null)
            {
                String hostSetMessage = String.format("Host set: %s", player.toString());
                LOGGER.log(Level.INFO, hostSetMessage);
                setHost(player);
            }
            this.players.add(player);
            String playersAddedMessage = String.format("Player added: %s", player.toString());
            LOGGER.log(Level.INFO, playersAddedMessage);
            return true;
        }
        catch(Exception e)
        {
            LOGGER.log(Level.WARNING, "Something went wrong in adding a player or setting the host", e);
            return false;
        }
    }

    @Override
    public String toString() {
        return name + ": (" + players.size() + "/" + MAX_SIZE+ ")";
    }

    /**
     * Gets the ip address of the host to connect to
     * @return a string containing the ip address of the host
     */
    public String getIpAddress()
    {
        return ipAddress;
    }
}
