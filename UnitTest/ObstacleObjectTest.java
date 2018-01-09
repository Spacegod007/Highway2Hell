import logic.game.ObstacleObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ObstacleObjectTest {

    private final ObstacleObject OO = new ObstacleObject();

    @Before
    public void setUp() throws Exception {
        //Nothing for now
    }

    /*
    TODO: FIX TEST WITH NEW RANDOM OBSTACLEOBJECT MECHANISM, SIZE (WIDTH/HEIGHT) IS NO LONGER GIVEN THROUGH THE CONSTRUCTOR
    @Test
    public void getWidth() throws Exception {
        assertEquals(100, OO.getWidth());
    }
    */

    @Test
    public void setWidth() throws Exception {
        assertEquals(100, OO.getWidth());
        OO.setWidth(200);
        assertEquals(200, OO.getWidth());
    }

    /*
    TODO: FIX TEST WITH NEW RANDOM OBSTACLEOBJECT MECHANISM, SIZE (WIDTH/HEIGHT) IS NO LONGER GIVEN THROUGH THE CONSTRUCTOR
    @Test
    public void getHeight() throws Exception {
        assertEquals(200d, OO.getHeight(), 0.001);
    }
    */

    @Test
    public void setHeight() throws Exception {
        assertEquals(200, OO.getHeight());
        OO.setHeight(300);
        assertEquals(300, OO.getHeight());
    }

}