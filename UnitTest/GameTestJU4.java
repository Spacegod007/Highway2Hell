import logic.Gamerule;
import logic.Gamerules;
import logic.game.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class GameTestJU4 {

    private final List<GameObject> gameObject = new ArrayList<>();
    private final Game game = new Game(new ArrayList<>(), new ArrayList<>());

    @Before
    public void setUp() throws Exception {
        gameObject.add(new PlayerObject(new Point(960, 900),new Size(78, 54),"Player1", CharacterColor.BLACK_BLUE));
        gameObject.add(new PlayerObject(new Point(860, 890),new Size(78, 54),"Player2", CharacterColor.BLACK_GREEN));
        gameObject.add(new PlayerObject(new Point(960, 700),new Size(78, 54),"Player1", CharacterColor.BLACK_RED));
        gameObject.add(new PlayerObject(new Point(860, 690),new Size(78, 54),"Player2", CharacterColor.BLACK_WHITE));
        gameObject.add(new ObstacleObject());
        game.setGameObjects(gameObject);
    }

    @Test
    public void getGameObjects() throws Exception {
        ArrayList<GameObject> testGameObject = new ArrayList<>();
        testGameObject.add(new PlayerObject(new Point(960, 900),new Size(78, 54),"Player1", CharacterColor.BLACK_BLUE));
        testGameObject.add(new PlayerObject(new Point(860, 890),new Size(78, 54),"Player2", CharacterColor.BLACK_RED));
        game.setGameObjects(testGameObject);

        assertEquals(2, game.getGameObjects().size());
    }

    @Test
    public void setGameObjects() throws Exception {
        ArrayList<GameObject> testGameObject = new ArrayList<>();
        testGameObject.add(new PlayerObject(new Point(960, 900),new Size(78, 54),"Player1", CharacterColor.BLACK_BLUE));
        testGameObject.add(new PlayerObject(new Point(860, 890),new Size(78, 54),"Player2", CharacterColor.BLACK_RED));
        testGameObject.add(new PlayerObject(new Point(860, 790),new Size(78, 54),"Player2", CharacterColor.BLACK_WHITE));
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
        assertEquals(false, isNotDead.getIsDead());

        //Assert Player Death per border.
        game.getGameObjects().get(0).setAnchor(new Point(-100, 100));
        PlayerObject po = (PlayerObject)game.getGameObjects().get(0);
        game.update();
        assertEquals(true, po.getIsDead());

        game.getGameObjects().get(1).setAnchor(new Point(1980, 100));
        PlayerObject po2 = (PlayerObject)game.getGameObjects().get(1);
        game.update();
        assertEquals(true, po2.getIsDead());

        game.getGameObjects().get(2).setAnchor(new Point(100, 1100));
        PlayerObject po3 = (PlayerObject)game.getGameObjects().get(2);
        game.update();
        assertEquals(true, po3.getIsDead());

        game.getGameObjects().get(3).setAnchor(new Point(100, -100));
        PlayerObject po4 = (PlayerObject)game.getGameObjects().get(3);
        game.update();
        assertEquals(true, po4.getIsDead());
    }

    @Test
    public void convertAccountsToPlayerObjects() throws Exception {
        //Method not implemented
        try {
            game.convertAccountsToPlayerObjects();
            Assert.fail();
        }
        catch(UnsupportedOperationException e){
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void ReturnPlayerObjects() throws Exception {
        //Assert the default value
        assertEquals(4, game.returnPlayerObjects().size());

        //Change the value
        List<GameObject> GO = new ArrayList<>();
        GO.add(new PlayerObject(new Point(0,0),new Size(78, 54), "lol", CharacterColor.BLACK_RED));
        game.setGameObjects(GO);

        //Assert the new value
        assertEquals(1, game.returnPlayerObjects().size());
    }

    @Test
    public void ReturnObstacleObjects() throws Exception {
        //Assert the default value
        assertEquals(1, game.returnObstacleObjects().size());

        //Change the value
        List<GameObject> GO = new ArrayList<>();
        GO.add(new ObstacleObject());
        GO.add(new ObstacleObject());
        game.setGameObjects(GO);

        //Assert the new value
        assertEquals(2, game.returnObstacleObjects().size());
    }

    @Test
    public void SetGameRules() throws Exception {
        //Assert default value
        assertEquals(0, game.getGamerules().size());

        List<Gamerule> GR = new ArrayList<>();
        GR.add(new Gamerule(Gamerules.GAMERULE1));
        game.setGamerules(GR);

        //Assert new value
        assertEquals(1, game.getGamerules().size());
        assertEquals(Gamerules.GAMERULE1, game.getGamerules().get(0).getGamerules());
    }

    @Test
    public void GetGameRules() throws Exception {
        //Change 1
        List<Gamerule> GR = new ArrayList<>();
        GR.add(new Gamerule(Gamerules.GAMERULE2));
        game.setGamerules(GR);

        //Assert first change
        assertEquals(Gamerules.GAMERULE2, game.getGamerules().get(0).getGamerules());

        //Change 2
        GR.clear();
        GR.add(new Gamerule(Gamerules.GAMERULE1));

        //Assert second change
        assertEquals(Gamerules.GAMERULE1, game.getGamerules().get(0).getGamerules());
    }

    @Test
    public void moveCharacter() throws Exception {
        PlayerObject PO = game.moveCharacter("Player1", Direction.LEFT);
        assertEquals(170d, PO.getCurrentRotation(), 0.0001);

        PO = game.moveCharacter("Player1", Direction.RIGHT);
        assertEquals(190d, PO.getCurrentRotation(), 0.0001);
    }

}