import javafx.scene.paint.Color;
import logic.game.Direction;
import logic.game.PlayerObject;
import logic.game.Point;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class PlayerObjectTest {

    private final PlayerObject PO = new PlayerObject(new Point(0,0), "Player1", Color.BLACK);

    @Before
    public void setUp() throws Exception {
        PO.setDistance(100);
        PO.setCurrentRotation(180d);
        PO.setPlayerSize(new double[]{10,11});
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
    public void getisDead() throws Exception {
        assertEquals(false, PO.getisDead());
        PO.setIsDead(true);
        assertEquals(true, PO.getisDead());
    }

    @Test
    public void setIsDead() throws Exception {
        assertEquals(false, PO.getisDead());
        PO.setIsDead(true);
        assertEquals(true, PO.getisDead());
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
        assertEquals(10d, PO.getPlayerSize()[0], 0.001);
        assertEquals(11d, PO.getPlayerSize()[1], 0.001);
    }

    @Test
    public void setPlayerSize() throws Exception {
        assertEquals(10d, PO.getPlayerSize()[0], 0.001);
        PO.setPlayerSize(new double[]{5,6});
        assertEquals(5d, PO.getPlayerSize()[0], 0.001);
        assertEquals(6d, PO.getPlayerSize()[1], 0.001);
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
    }

}