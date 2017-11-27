import javafx.scene.paint.Color;
import logic.game.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class GameTestJU4 {

    private final List<GameObject> gameObject = new ArrayList<>();
    private final Game game = new Game(new ArrayList<>());

    @Before
    public void setUp() throws Exception {
        gameObject.add(new PlayerObject(new Point(960, 900),"Player1", Color.BLACK));
        gameObject.add(new PlayerObject(new Point(860, 890),"Player2", Color.RED));
        gameObject.add(new PlayerObject(new Point(960, 700),"Player1", Color.GREEN));
        gameObject.add(new PlayerObject(new Point(860, 690),"Player2", Color.BLUE));
        game.setGameObjects(gameObject);
    }

    @Test
    public void getGameObjects() throws Exception {
        ArrayList<GameObject> testGameObject = new ArrayList<>();
        testGameObject.add(new PlayerObject(new Point(960, 900),"Player1", Color.BLACK));
        testGameObject.add(new PlayerObject(new Point(860, 890),"Player2", Color.RED));
        game.setGameObjects(testGameObject);

        assertEquals(2, game.getGameObjects().size());
    }

    @Test
    public void setGameObjects() throws Exception {
        ArrayList<GameObject> testGameObject = new ArrayList<>();
        testGameObject.add(new PlayerObject(new Point(960, 900),"Player1", Color.BLACK));
        testGameObject.add(new PlayerObject(new Point(860, 890),"Player2", Color.RED));
        testGameObject.add(new PlayerObject(new Point(860, 790),"Player2", Color.GREEN));
        game.setGameObjects(testGameObject);

        assertEquals(3, game.getGameObjects().size());
    }

    @Test
    public void update() throws Exception {
        //Asserting the default value for the anchor y of the PlayerObject
        assertEquals(900d, game.getGameObjects().get(0).getAnchor().getY(), 0.0001);

        //Change de anchor point
        game.update();

        //Re-Assert
        assertEquals(901.5d, game.getGameObjects().get(0).getAnchor().getY(), 0.0001);

        //Assert isDead is false
        PlayerObject isNotDead = (PlayerObject)game.getGameObjects().get(0);
        assertEquals(false, isNotDead.getisDead());

        //Assert Player Death per border.
        game.getGameObjects().get(0).setAnchor(new Point(-100, 100));
        PlayerObject po = (PlayerObject)game.getGameObjects().get(0);
        game.update();
        assertEquals(true, po.getisDead());

        game.getGameObjects().get(1).setAnchor(new Point(1980, 100));
        PlayerObject po2 = (PlayerObject)game.getGameObjects().get(1);
        game.update();
        assertEquals(true, po2.getisDead());

        game.getGameObjects().get(2).setAnchor(new Point(100, 1100));
        PlayerObject po3 = (PlayerObject)game.getGameObjects().get(2);
        game.update();
        assertEquals(true, po3.getisDead());

        game.getGameObjects().get(3).setAnchor(new Point(100, -100));
        PlayerObject po4 = (PlayerObject)game.getGameObjects().get(3);
        game.update();
        assertEquals(true, po4.getisDead());
    }

    @Test
    public void convertAccountsToPlayerObjects() throws Exception {
        //Method not implemented
        try {
            game.convertAccountsToPlayerObjects();
            Assert.fail();
        }
        catch(UnsupportedOperationException e){
        }
    }

    @Test
    public void endGame() throws Exception {

    }

    @Test
    public void run() throws Exception {

    }

    @Test
    public void moveCharacter() throws Exception {
        PlayerObject PO = game.moveCharacter("Player1", Direction.LEFT);
        assertEquals(170d, PO.getCurrentRotation(), 0.0001);

        PO = game.moveCharacter("Player1", Direction.RIGHT);
        assertEquals(190d, PO.getCurrentRotation(), 0.0001);
    }

    @Test
    public void setGameRules() throws Exception {
        //Gamerules are not present yet, nothing to test.
    }

    @Test
    public void getGameRules() throws Exception {
        //Gamerules are not present yet, nothing to test.
    }
}