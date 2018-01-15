package logic.game;

import javafx.scene.paint.Color;
import logic.Gamerule;
import logic.administration.User;

import java.util.*;

/**
 * A class which manages the game while the game is being played
 */
public class Game
{
    /**
     * A list of all game objects including players, obstacles, etc.
     */
    private List<GameObject> gameObjects = new ArrayList<>();

    /**
     * A list of the currently bound game rules
     */
    private List<Gamerule> gameRules;

    /**
     * Determines the speed at which the game scrolls
     */
    private final double scrollSpeed = 1.5;

    /**
     * The amount of obstacles in the game
     */
    private final int obstacleCount = 1;

    private final Timer timer;
    private boolean timerRunning = false;

    private final Object synchronizer;

    private final int amountOfObstacleObjects = 8;

    /**
     * Constructs the game object
     * @param gameRules the game rules that are bound to this current game
     */
    public Game(List<Gamerule> gameRules, List<User> users)
    {
        synchronizer = new Object();

        List<GameObject> rmiGameObjects = new ArrayList<>();
        int j = 0;
        for(User u : users)
        {
            System.out.println(u.getUsername());
            rmiGameObjects.add(new PlayerObject(new Point(600 + j, 900),new Size(78, 54),u.getUsername(), Color.BLACK));
            j = j+50;
        }
        this.gameRules = gameRules;
        gameObjects = rmiGameObjects;
        addObstacles();

        //Add players here
        //gameObjects.add(new PlayerObject(new Point(600, 900),name, Color.BLACK));
        //gameObjects.add(new PlayerObject(new Point(540, 900),"Player2", Color.BLACK));

        //Adds obstacles
        for (int i=0; i<obstacleCount; i++)
        {
            gameObjects.add(new ObstacleObject());
            System.out.println("item " + i + " added");
        }

        timer = new Timer();
    }

    private void addObstacles()
    {
        for (int i = 0; i < amountOfObstacleObjects; i++)
        {
            gameObjects.add(new ObstacleObject());
        }
    }

    public void startGame()
    {
        synchronized (synchronizer)
        {
            if (!timerRunning)
            {
                timerRunning = true;
                timer.scheduleAtFixedRate(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        update();
                    }
                }, 250, 30);
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
        //todo change to set player objects

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
            for (GameObject GO : getGameObjects())
            {
                //GO scroll.
                if (GO instanceof ObstacleObject)
                {
                    GO.setAnchor(new Point(GO.getAnchor().getX(), GO.getAnchor().getY() + scrollSpeed * 3));
                } else
                {
                    GO.setAnchor(new Point(GO.getAnchor().getX(), GO.getAnchor().getY() + scrollSpeed));
                }

                //Check if GO is dead.
                //Game window: 1200x1000
                //Character size 52x36
                if (GO instanceof PlayerObject)
                {
                    //Setting the borders of the map for player death.
                    //Might need some tweaking, leave to the tester.
                    PlayerObject PO = (PlayerObject) GO;
                    Point anchor = PO.getAnchor();
                    Size size = PO.getPlayerSize();

                    if (anchor.getX() + size.getHeight() < 0 || anchor.getX() > 1200 || anchor.getY() + (size.getHeight() / 2) > 1000 || anchor.getY() + (size.getHeight() / 2) < 0)
                    {
                        PO.setIsDead(true);
                    }

                    for (GameObject GO2 : gameObjects)
                    {
                        if (GO2 instanceof ObstacleObject && PO.checkForObstacleCollision((ObstacleObject) GO2))
                        {
                            PO.setIsDead(true);
                            //System.out.println("RIP");
                        }
                    }
                }

                if (GO instanceof ObstacleObject)
                {
                    ObstacleObject OO = (ObstacleObject) GO;
                    if (OO.getAnchor().getY() + (OO.getHeight()) > 1000)
                    {
                        gameObjects.set(index, new ObstacleObject());
                    }
                }
                index++;
            }
        }
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
        //Scene newScene, Stage stage todo move to client
        synchronized (synchronizer)
        {
            for (GameObject GO : getGameObjects())
            {
                if (GO instanceof PlayerObject)
                {
                    //stage.setScene(newScene); todo move to client
                    returnable.add((PlayerObject) GO);
                    //System.out.println("Player: " + PO.getName() + " = " + PO.getDistance() + " Points"); todo move to client
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

                    if (playerName.equals("Player1") && p.getName().equals("Player1"))
                    {
                        switch (direction)
                        {
                            case LEFT:
                                p.move(Direction.LEFT);
                                break;
                            case RIGHT:
                                p.move(Direction.RIGHT);
                                break;
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
}
