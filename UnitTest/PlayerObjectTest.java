import logic.game.*;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class PlayerObjectTest {

    private long defaultDistance;
    private double defaultRotation;

    private Size size;

    private String name;

    private CharacterColor characterColor;

    private PlayerObject PO;

    @Before
    public void setUp() throws Exception {

        defaultDistance = 0;
        defaultRotation = 180;

        double x = 1;
        double y = 2;
        Point anchor = new Point(x, y);

        double width = 50;
        double height = 60;
        size = new Size(width, height);

        name = "name";

        characterColor = CharacterColor.BLACK_BLUE;

        PO = new PlayerObject(anchor, size, name, characterColor);

        PO.setDistance(defaultDistance);
        PO.setCurrentRotation(defaultRotation);
        PO.setColor(characterColor);
    }

    @Test
    public void getDistance() throws Exception {
        assertEquals("Player distance should be 0 by default",defaultDistance, PO.getDistance());
    }

    @Test
    public void setDistance() throws Exception {
        assertEquals("Player distance should be 0 by default",defaultDistance, PO.getDistance());

        long newDistance = 50;

        PO.setDistance(newDistance);

        assertEquals("Player distance should be the set value", newDistance, PO.getDistance());
    }

    @Test
    public void getIsDead() throws Exception {
        assertEquals("Players should not be dead by default",false, PO.getIsDead());
    }

    @Test
    public void setIsDead() throws Exception {
        assertEquals("Players should not be dead by default",false, PO.getIsDead());

        boolean newIsDead = true;

        PO.setIsDead(true);

        assertEquals("Players should be dead when set dead", true, PO.getIsDead());
    }

    @Test
    public void getCurrentRotation() throws Exception {
        assertEquals("rotation is not the default rotation", defaultRotation, PO.getCurrentRotation(), 0.001);
    }

    @Test
    public void setCurrentRotation() throws Exception {
        assertEquals("rotation is not the default rotation", defaultRotation, PO.getCurrentRotation(), 0.001);

        double newRotation = 170;

        PO.setCurrentRotation(newRotation);

        assertEquals("rotation is not the set rotation", newRotation, PO.getCurrentRotation(), 0.001);
    }

    @Test
    public void getPlayerSize() throws Exception {
        assertEquals("size value is not equal to the given value in the constructor", size, PO.getSize());
    }

    @Test
    public void setPlayerSize() throws Exception {
        assertEquals("size value is not equal to the given value in the constructor", size, PO.getSize());

        Size newSize = new Size(5, 6);

        PO.setPlayerSize(newSize);

        assertEquals("size value is not equal to the set size", newSize, PO.getSize());
    }

    @Test
    public void getName() throws Exception {
        assertEquals("name value is not equal to the value given in the constructor", name, PO.getName());
    }

    @Test
    public void setName() throws Exception {
        assertEquals("name value is not equal to the value given in the constructor", name, PO.getName());

        String newName = "another name";

        PO.setName(newName);
        assertEquals("name value is not equal to the set value", newName, PO.getName());
    }

    @Test
    public void getColor() throws Exception {
        assertEquals("character color is not equal to the default value", characterColor, PO.getColor());
    }

    @Test
    public void setColor() throws Exception {
        assertEquals("character color is not equal to the default value", characterColor, PO.getColor());

        CharacterColor newColor = CharacterColor.BLACK_GREEN;

        PO.setColor(newColor);

        assertEquals("character color value is not equal to the set value", newColor, PO.getColor());
    }

    @Test
    public void move() throws Exception {
        double leftRotation = 170;
        double rightRotation = 190;

        PO.move(Direction.LEFT);
        assertEquals("left rotation should be 170 degrees", leftRotation, PO.getCurrentRotation(), 0.001);

        PO.move(Direction.RIGHT);
        assertEquals("right rotation should be 190 degrees", rightRotation, PO.getCurrentRotation(), 0.001);
    }

    @Test
    public void CheckForObstacleCollision() throws Exception {
        ObstacleObject OO = new ObstacleObject();

        //Assert default.
        assertEquals("Objects should not collide if they don't touch each other", false, PO.checkForObstacleCollision(OO));

        Point playerAnchor = PO.getAnchor();
        Point obstacleAnchor = OO.getAnchor();
        playerAnchor.setX(obstacleAnchor.getX());
        playerAnchor.setY(obstacleAnchor.getY());

        //Change the anchor
        assertEquals("Objects should collide if they touch each other", true, PO.checkForObstacleCollision(OO));
    }
}