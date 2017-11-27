package logic.administration;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import logic.Gamerule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lobby implements Runnable, Serializable
{
    /**
     * The id of the lobby
     */
    private int id;

    /**
     * The host of the lobby
     */
    private User host;

    /**
     * The name of the lobby
     */
    private String name;

    /**
     * The players that are currently in the lobby
     */
    private List<User> players;

    /**
     * The gamerules bound to this lobby by the host
     */
    private List<Gamerule> gamerules;

    /**
     * The ipAddress used to connect to the server when the game starts
     */
    private String ipAddress;

    /**
     * The port the server will be run on
     */
    private final int port = 1111;

    /**
     * The maximum amount of players allowed
     */
    private static int maxSize = 64; // static?

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

    /**
     * Sets the host of this lobby
     * @param host to be set
     */
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
     * Gets a list of all the gamerules bound to this lobby
     * @return a list of all gamerules bound to this lobby
     */
    public List<Gamerule> getGamerules()
    {
        return Collections.unmodifiableList(gamerules);
    }

    /**
     * Gets the name of this lobby
     * @return
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
        gamerules = new ArrayList<>();
        this.name = name;
        this.id = id;
        this.ipAddress = ipAddress;
    }

    /**
     * Removes the specified userId from the lobby
     * @param userId of the user who will be removed from the lobby
     * @return true if the user was removed, false if the user was not removed
     */
    public boolean leave(int userId)
    {
        try
        {
            for(User p : players){
                if(p.getID() == userId)
                {
                    this.players.remove(p);
                    return true;
                }
            }
            return false;
        }
        catch(Exception e)
        {
            return false;
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
                System.out.println("Host set: " + player.toString());
                setHost(player);
            }
            this.players.add(player);
            System.out.println("Player added: " + player.toString());
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    /**
     * Changes the gamerules
     */
    public void editGameRules()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Starts the game
     */
    public void startGame()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Migrates the host if the host left the lobby
     */
    public void migrateHost()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return name + ": (" + players.size() + "/" + Lobby.maxSize + ")";
    }

    @Override
    // TODO
    // dit gebruiken we helemaal niet
    public void run() {
        try{while(true){
           if(false){
               break;
           }
        }}
        catch(Exception ignored){}
    }

}
