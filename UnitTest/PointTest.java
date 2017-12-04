import logic.game.Point;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PointTest {
    private final Point p = new Point(10,10);

    @Before
    public void setUp() throws Exception {
        //Nothing for now.
    }

    @Test
    public void getX() throws Exception {
        //Assert default
        assertEquals(10d, p.getX(), 0.001);

        //Change
        p.setX(20d);

        //Asset changed
        assertEquals(20d, p.getX(), 0.001);
    }

    @Test
    public void setX() throws Exception {
        //Assert default
        assertEquals(10d, p.getX(), 0.001);

        //Change
        p.setX(20d);

        //Asset changed
        assertEquals(20d, p.getX(), 0.001);
    }

    @Test
    public void getY() throws Exception {
        //Assert default
        assertEquals(10d, p.getY(), 0.001);

        //Change
        p.setY(20d);

        //Asset changed
        assertEquals(20d, p.getY(), 0.001);
    }

    @Test
    public void setY() throws Exception {
        //Assert default
        assertEquals(10d, p.getY(), 0.001);

        //Change
        p.setY(20d);

        //Asset changed
        assertEquals(20d, p.getY(), 0.001);
    }

    @Test
    public void tostring() throws Exception {
        assertEquals("x=10.0, y=10.0", p.toString());
    }

}