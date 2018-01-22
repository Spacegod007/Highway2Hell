package logic.game;

import logic.Gamerule;
import logic.administration.User;
import logic.fontyspublisher.IRemotePublisherForDomain;

import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class which manages the game while the game is being played
 */
public class Game
{
    private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

    /**
     *
     */
    private IRemotePublisherForDomain publisher = null;

    /**
     * A list of all game objects including players, obstacles, etc.
     */
    private List<GameObject> gameObjects = new ArrayList<>();

    /**
     * A list of the currently bound game rules
     */
    private List<Gamerule> gameRules;

    /**
     * The timer which updates the game
     */
    private final Timer timer;

    /**
     * Checks if the timer is running
     */
    private boolean timerRunning = false;

    /**
     * Synchronises communication over multiple threads
     */
    private final Object synchronizer;

    /**
     * Constructs the game object
     * @param gameRules the game rules that are bound to this current game
     */
    public Game(List<Gamerule> gameRules, List<User> users)
    {
        synchronizer = new Object();

        List<GameObject> rmiGameObjects = new ArrayList<>();
        int j = 0;
        String usersInGame = String.format("Users in game: %s", users);
        LOGGER.log(Level.INFO, usersInGame);
        for(User u : users)
        {
            rmiGameObjects.add(new PlayerObject(new Point(600 + j, 900),new Size(78, 54),u.getUsername(), u.getCharacterColor()));
            j = j+50;
        }
        this.gameRules = gameRules;
        gameObjects = rmiGameObjects;
        addObstacles();

        //Adds obstacles
        /*
      The amount of obstacles in the game
     */
        int obstacleCount = 1;
        for (int i = 0; i< obstacleCount; i++)
        {
            gameObjects.add(new ObstacleObject());
            String itemAdded = String.format("Item %s was added", i);
            LOGGER.log(Level.INFO, itemAdded);
        }

        timer = new Timer();
    }

    /**
     * Adds an obstacle object to the game
     */
    private void addObstacles()
    {
        /*
      The total amount of obstacle objects allowed at any given time
     */
        int amountOfObstacleObjects = 8;
        for (int i = 0; i < amountOfObstacleObjects; i++)
        {
            gameObjects.add(new ObstacleObject());
        }
    }

    public void addObstacle()
    {
        gameObjects.add(new ObstacleObject());
    }

    /**
     * Starts the game
     */
    public void startGame()
    {
        synchronized (synchronizer)
        {
            LOGGER.log(Level.INFO, "Starting game");
            if (!timerRunning)
            {
                timerRunning = true;
                timer.scheduleAtFixedRate(new TimerTask()
                {
                    @Override
                    public void run()
                    {

                        update();
                        if(gameObjects != null && !gameObjects.isEmpty())
                        {
                            try
                            {
                                publisher.inform("gameObjects", null, gameObjects);
                            } catch (RemoteException e)
                            {
                                LOGGER.log(Level.SEVERE, "Error in connection", e);
                            }
                        }
                        else
                        {
                            LOGGER.log(Level.WARNING, "no game objects found");
                        }
                    }
                }, 250, 17);

                timer.scheduleAtFixedRate(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        synchronized (synchronizer)
                        {
                            addObstacle();
                        }
                    }
                }, 10000, 10000);
            }
        }
    }

    /**
     * Gets a list of all game objects
     * @return the lis of all game objects
     */
    public List<GameObject> getGameObjects()
    {
        return gameObjects;
    }

    /**
     * Sets the list of game objects
     * @param gameObjects to be set
     */
    public void setGameObjects(List<GameObject> gameObjects)
    {
        synchronized (synchronizer)
        {
            this.gameObjects = gameObjects;
        }
    }

    /**
     * Gets a list of all game rules
     * @return the list of game rules
     */
    public List<Gamerule> getGamerules()
    {
        synchronized (synchronizer)
        {
            return gameRules;
        }
    }

    /**
     * Sets the list of game rules
     * @param gameRules to be set
     */
    public void setGamerules(List<Gamerule> gameRules)
    {
        synchronized (synchronizer)
        {
            this.gameRules = gameRules;
        }
    }

    /**
     * Calculates all necessary information for the next frame
     */
    public void update()
    {
        synchronized (synchronizer)
        {
            int index = 0;
            //Method for scrolling the screen.
            for (GameObject gameObject : gameObjects)
            {
                //gameObject scroll.
                /*
      Determines the speed at which the game scrolls
     */
                double scrollSpeed = 1.5;
                if (gameObject instanceof ObstacleObject)
                {
                    gameObject.setAnchor(new Point(gameObject.getAnchor().getX(), gameObject.getAnchor().getY() + scrollSpeed * 3));
                } else //gameObject = PlayerObject
                {
                    gameObject.setAnchor(new Point(gameObject.getAnchor().getX(), gameObject.getAnchor().getY() + scrollSpeed));
                }

                //Check if gameObject is dead.
                //Game window: 1200x1000
                //Character size 52x36
                if (gameObject instanceof PlayerObject)
                {
                    //Setting the borders of the map for player death.
                    //Might need some tweaking, leave to the tester.
                    PlayerObject playerObject = (PlayerObject) gameObject;
                    Point anchor = playerObject.getAnchor();
                    Size size = playerObject.getPlayerSize();

                    if (anchor.getX() + size.getHeight() < 0 || anchor.getX() > 1200 || anchor.getY() + (size.getHeight() / 2) > 1000 || anchor.getY() + (size.getHeight() / 2) < 0)
                    {
                        playerObject.setIsDead(true);
                    }

                    for (GameObject collisionGameObject : gameObjects)
                    {
                        if (collisionGameObject instanceof ObstacleObject && playerObject.checkForObstacleCollision((ObstacleObject) collisionGameObject))
                        {
                            playerObject.setIsDead(true);
                        }
                    }
                }

                if (gameObject instanceof ObstacleObject)
                {
                    ObstacleObject obstacleObject = (ObstacleObject) gameObject;
                    if (obstacleObject.getAnchor().getY() + (obstacleObject.getHeight()) > 1000)
                    {
                        ObstacleObject temp = new ObstacleObject();
                        obstacleObject.setAnchor(temp.getAnchor());
                        gameObjects.set(index, obstacleObject);
                    }
                }
                index++;

                if(checkAllDead())
                {
                    try
                    {
                        if (publisher != null)
                        {
                            publisher.inform("allDead", null, true);
                        }
                    }
                    catch (RemoteException e)
                    {
                        LOGGER.log(Level.SEVERE, "Error in connection", e);
                    }
                }
            }
        }
    }

    /**
     * Checks if all players are dead
     * @return true if all players are dead otherwise false
     */
    private boolean checkAllDead()
    {
        for (GameObject go : gameObjects)
        {
            if (go instanceof PlayerObject && !((PlayerObject) go).getIsDead())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts an account to a player object
     */
    public void convertAccountsToPlayerObjects()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Ends the game
     */
    public List<PlayerObject> endGame()
    {
        timer.cancel();
        List<PlayerObject> returnable = new ArrayList<>();
        synchronized (synchronizer)
        {
            for (GameObject GO : getGameObjects())
            {
                if (GO instanceof PlayerObject)
                {
                    returnable.add((PlayerObject) GO);
                }
            }
        }
        return returnable;
    }

    /**
     * Moves the character in a specified direction
     * @param playerName of the character to be moved
     * @param direction in which the character needs to be moved
     * @return The player with new values
     */
    public PlayerObject moveCharacter(String playerName, Direction direction)
    {
        synchronized (synchronizer)
        {
            for (GameObject g : gameObjects)
            {
                if (g instanceof PlayerObject)
                {
                    PlayerObject p = (PlayerObject) g;

                    if (p.getName().equals(playerName))
                    {
                        if (direction == Direction.LEFT)
                        {
                            p.move(Direction.LEFT);
                        }
                        else if (direction == Direction.RIGHT)
                        {
                            p.move(Direction.RIGHT);

                        }
                        return p;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Should never be thrown");
    }

    /**
     * Gets a list of the current player objects
     * @return a list of all player objects
     */
    public List<PlayerObject> returnPlayerObjects()
    {
        List<PlayerObject> listToReturn = new ArrayList<>();

        synchronized (synchronizer)
        {
            for (GameObject GO : gameObjects)
            {
                if (GO instanceof PlayerObject)
                {
                    listToReturn.add((PlayerObject) GO);
                }
            }
        }

        return listToReturn;
    }

    /**
     * Gets a list of the current obstacle objects
     * @return a list of all obstacle objects
     */
    public List<ObstacleObject> returnObstacleObjects()
    {
        List<ObstacleObject> listToReturn = new ArrayList<>();

        synchronized (synchronizer)
        {
            for (GameObject GO : gameObjects)
            {
                if (GO instanceof ObstacleObject)
                {
                    listToReturn.add((ObstacleObject) GO);
                }
            }
        }

        return listToReturn;
    }

    /**
     * Sets the remote publisher for all participating clients
     * @param rpd the publisher for the domain
     */
    public void setRpd(IRemotePublisherForDomain rpd)
    {
        this.publisher = rpd;
    }
}
