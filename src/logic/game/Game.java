package logic.game;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.Gamerule;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Game implements Runnable, Observer {
    private List<GameObject> GameObjects;
    private List<Gamerule> gamerules;
    private double scrollSpeed = 1.5;
    private int obstacleCount = 8;

    public Game(List<Gamerule> gamerules) {
        this.gamerules = gamerules;
        GameObjects = new ArrayList<>();

        //Add players here
        GameObjects.add(new PlayerObject(new Point(600, 900),"Player1", Color.BLACK));
        //GameObjects.add(new PlayerObject(new Point(540, 900),"Player2", Color.BLACK));

        for(int i=0; i<obstacleCount; i++){
            GameObjects.add(new ObstacleObject(70, 48));
            System.out.println("item " + i + " added");
        }
    }

    public List<GameObject> getGameObjects() {
        return GameObjects;
    }

    public void setGameObjects(List<GameObject> gameObjects) {
        GameObjects = gameObjects;
    }

    public List<Gamerule> getGamerules() {
        return gamerules;
    }

    public void setGamerules(List<Gamerule> gamerules) {
        this.gamerules = gamerules;
    }

    public void update(){
        int index = 0;
        //Method for scrolling the screen.
        for(GameObject GO : getGameObjects())
        {
            //GO scroll.
            if(GO.getClass() != ObstacleObject.class)
            {
                GO.setAnchor(new Point(GO.getAnchor().getX(), GO.getAnchor().getY() + scrollSpeed));
            }
            else{
                GO.setAnchor(new Point(GO.getAnchor().getX(), GO.getAnchor().getY() + scrollSpeed * 3));
            }

            //Check if GO is dead.
            //Game window: 1200x1000
            //Character size 52x36
            if(GO.getClass() == PlayerObject.class)
            {
                //Setting the borders of the map for player death.
                //Might need some tweaking, leave to the tester.
                PlayerObject PO = (PlayerObject)GO;
                Point anchor = PO.getAnchor();
                double[] size = PO.getPlayerSize();

                if(anchor.getX() + size[1] < 0 || anchor.getX() > 1200 || anchor.getY() + (size[1]/2) > 1000 || anchor.getY() + (size[1]/2) < 0)
                {
                    PO.setIsDead(true);
                }

                for (GameObject GO2: GameObjects) {
                    if(GO2.getClass() == ObstacleObject.class && PO.checkForObstacleCollision((ObstacleObject) GO2)) {
                        PO.setIsDead(true);
                        //System.out.println("RIP");
                    }
                }
            }

            if (GO.getClass() == ObstacleObject.class)
            {
                ObstacleObject OO = (ObstacleObject) GO;
                if(OO.getAnchor().getY() + (OO.getHeight()) > 1000)
                {
                    GameObjects.set(index, new ObstacleObject(70, 48));
                }
            }
            index++;
        }
        index = 0;
    }
    public void convertAccountsToPlayerObjects(){
        throw new UnsupportedOperationException();
    }
    public void endGame(Scene newScene, Stage stage)
    {
        for(GameObject GO : getGameObjects())
        {
            if(GO.getClass() == PlayerObject.class)
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

    public PlayerObject moveCharacter(String playerName, Direction direction) {
        for (GameObject g : GameObjects) {
            if (g.getClass() == PlayerObject.class)
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

    public ArrayList<PlayerObject> returnPlayerObjects()
    {
        ArrayList<PlayerObject> listToReturn = new ArrayList<>();
        for (GameObject GO : GameObjects)
        {
            if (GO.getClass() == PlayerObject.class)
            {
                listToReturn.add((PlayerObject)GO);
            }
        }
        return listToReturn;
    }

    public ArrayList<ObstacleObject> returnObstacleObjects()
    {
        ArrayList<ObstacleObject> listToReturn = new ArrayList<>();
        for (GameObject GO : GameObjects)
        {
            if (GO.getClass() == ObstacleObject.class)
            {
                listToReturn.add((ObstacleObject)GO);
            }
        }
        return listToReturn;
    }
}
