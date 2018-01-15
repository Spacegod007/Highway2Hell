import javafx.scene.paint.Color;
import logic.game.Direction;
import logic.game.PlayerObject;
import logic.game.Point;
import logic.game.Size;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class PlayerObjectTest {

    private final PlayerObject PO = new PlayerObject(new Point(0,0),new Size(78, 54), "Player1", Color.BLACK);

    @Before
    public void setUp() throws Exception {
        PO.setDistance(100);
        PO.setCurrentRotation(180d);
        PO.setAnchor(new Point(50,50));
        PO.setPlayerSize(new Size(10, 11));
    }

    @Test
    public void getDistance() throws Exception {
        assertEquals(100, PO.getDistance());
    }

    @Test
    public void setDistance() throws Exception {
        PO.setDistance(50);
        assertEquals(50, PO.getDistance());
    }

    @Test
    public void getIsDead() throws Exception {
        assertEquals(false, PO.getIsDead());
        PO.setIsDead(true);
        assertEquals(true, PO.getIsDead());
    }

    @Test
    public void setIsDead() throws Exception {
        assertEquals(false, PO.getIsDead());
        PO.setIsDead(true);
        assertEquals(true, PO.getIsDead());
    }

    @Test
    public void getCurrentRotation() throws Exception {
        assertEquals(180d, PO.getCurrentRotation(), 0.001);
    }

    @Test
    public void setCurrentRotation() throws Exception {
        assertEquals(180d, PO.getCurrentRotation(), 0.001);
        PO.setCurrentRotation(170d);
        assertEquals(170d, PO.getCurrentRotation(), 0.001);
    }

    @Test
    public void getPlayerSize() throws Exception {
        assertEquals(10d, PO.getPlayerSize().getWidth(), 0.001);
        assertEquals(11d, PO.getPlayerSize().getHeight(), 0.001);
    }

    @Test
    public void setPlayerSize() throws Exception {
        assertEquals(10d, PO.getPlayerSize().getWidth(), 0.001);
        PO.setPlayerSize(new Size(5, 6));
        assertEquals(5d, PO.getPlayerSize().getWidth(), 0.001);
        assertEquals(6d, PO.getPlayerSize().getHeight(), 0.001);
    }

    @Test
    public void getName() throws Exception {
        assertEquals("Player1", PO.getName());
    }

    @Test
    public void setName() throws Exception {
        assertEquals("Player1", PO.getName());
        PO.setName("Player2");
        assertEquals("Player2", PO.getName());
    }

    @Test
    public void getColor() throws Exception {
        assertEquals(Color.BLACK, PO.getColor());
    }

    @Test
    public void setColor() throws Exception {
        assertEquals(Color.BLACK, PO.getColor());
        PO.setColor(Color.WHITE);
        assertEquals(Color.WHITE, PO.getColor());
    }

    @Test
    public void move() throws Exception {
        PO.move(Direction.LEFT);
        assertEquals(170d, PO.getCurrentRotation(), 0.001);

        PO.move(Direction.RIGHT);
        assertEquals(190d, PO.getCurrentRotation(), 0.001);
    }

    /*
    TODO: FIX TEST WITH NEW RANDOM OBSTACLE OBJECT MECHANISM, SIZE (WIDTH/HEIGHT) IS NO LONGER GIVEN THROUGH THE CONSTRUCTOR
    @Test
    public void CheckForObstacleCollision() throws Exception {
        ObstacleObject OO = new ObstacleObject();

        //Assert default.
        assertEquals(false, PO.checkForObstacleCollision(OO));

        //Change the anchor
        OO.setAnchor(new Point(0,0));
        assertEquals(true, PO.checkForObstacleCollision(OO));
    }
    */
}