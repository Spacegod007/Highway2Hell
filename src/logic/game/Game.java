package logic.game;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.Gamerule;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A class which manages the game while the game is being played
 */
public class Game implements Runnable, Observer
{
    /**
     * A list of all game objects including players, obstacles, etc.
     */
    private List<GameObject> gameObjects;

    /**
     * A list of the currently bound gamerules
     */
    private List<Gamerule> gameRules;

    /**
     * Determines the speed at which the game scrolls
     */
    private double scrollSpeed = 1.5;

    /**
     * The amount of obstacles in the game
     */
    private int obstacleCount = 8;

    /**
     * Constructs the game object
     * @param gameRules the gamerules that are bound to this current game
     */
    public Game(List<Gamerule> gameRules)
    {
        this.gameRules = gameRules;
        gameObjects = new ArrayList<>();

        //Add players here
        gameObjects.add(new PlayerObject(new Point(600, 900),"Player1", Color.BLACK));
        //gameObjects.add(new PlayerObject(new Point(540, 900),"Player2", Color.BLACK));

        for (int i=0; i<obstacleCount; i++)
        {
            gameObjects.add(new ObstacleObject(70, 48));
            System.out.println("item " + i + " added");
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
        //todo make this useless statement do something, it is called 3 times and doesn't do anything
        this.gameObjects = gameObjects;
    }

    /**
     * Gets a list of all gamerules
     * @return the list of gamerules
     */
    public List<Gamerule> getGamerules()
    {
        return gameRules;
    }

    /**
     * Sets the list of gameruels
     * @param gameRules to be set
     */
    public void setGamerules(List<Gamerule> gameRules)
    {
        this.gameRules = gameRules;
    }

    /**
     * Calculates all necessary information for the next frame
     */
    public void update()
    {
        int index = 0;
        //Method for scrolling the screen.
        for(GameObject GO : getGameObjects())
        {
            //GO scroll.
            if (GO instanceof ObstacleObject)
            {
                GO.setAnchor(new Point(GO.getAnchor().getX(), GO.getAnchor().getY() + scrollSpeed * 3));
            }
            else
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
                PlayerObject PO = (PlayerObject)GO;
                Point anchor = PO.getAnchor();
                double[] size = PO.getPlayerSize();

                if (anchor.getX() + size[1] < 0 || anchor.getX() > 1200 || anchor.getY() + (size[1]/2) > 1000 || anchor.getY() + (size[1]/2) < 0)
                {
                    PO.setIsDead(true);
                }

                for (GameObject GO2: gameObjects)
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
                if(OO.getAnchor().getY() + (OO.getHeight()) > 1000)
                {
                    gameObjects.set(index, new ObstacleObject(70, 48));
                }
            }
            index++;
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
     * @param newScene to be shown
     * @param stage to be used
     */
    public void endGame(Scene newScene, Stage stage)
    {
        for(GameObject GO : getGameObjects())
        {
            if (GO instanceof PlayerObject)
            {
                stage.setScene(newScene);
                PlayerObject PO = (PlayerObject)GO;
                System.out.println("Player: " + PO.getName() + " = " + PO.getDistance() + " Points");
            }
        }
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * Moves the character in a specified direction
     * @param playerName of the character to be moved
     * @param direction in which the character needs to be moved
     * @return The player with new values
     */
    public PlayerObject moveCharacter(String playerName, Direction direction)
    {
        for (GameObject g : gameObjects)
        {
            if (g instanceof PlayerObject)
            {
                PlayerObject p = (PlayerObject) g;

                if (playerName == "Player1" && p.getName() == "Player1")
                {
                    switch (direction) {
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
        return null;
    }

    /**
     * Gets a list of the current player objects
     * @return a list of all player objects
     */
    public List<PlayerObject> returnPlayerObjects()
    {
        List<PlayerObject> listToReturn = new ArrayList<>();

        for (GameObject GO : gameObjects)
        {
            if (GO instanceof PlayerObject)
            {
                listToReturn.add((PlayerObject) GO);
            }
        }
        return listToReturn;
    }

    /**
     * Gets a list of the current obstacle objects
     * @return a list of all obtacle objects
     */
    public List<ObstacleObject> returnObstacleObjects()
    {
        List<ObstacleObject> listToReturn = new ArrayList<>();

        for (GameObject GO : gameObjects)
        {
            if (GO instanceof ObstacleObject)
            {
                listToReturn.add((ObstacleObject) GO);
            }
        }

        return listToReturn;
    }
}
