import logic.game.Point;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PointTest {

    private double x;
    private double y;

    private Point point;

    @Before
    public void setUp() throws Exception
    {
        x = 1;
        y = 2;

        point = new Point(x, y);
    }

    @Test
    public void getX() throws Exception {
        //Assert default
        assertEquals("x value is not the given value in the constructor", x, point.getX(), 0.001);
    }

    @Test
    public void setX() throws Exception {
        //Assert default
        assertEquals("x value is not the given value in the constructor", x, point.getX(), 0.001);

        double newX = 50;

        //Change
        point.setX(newX);

        //Asset changed
        assertEquals("x value is not the given value in the setX method", newX, point.getX(), 0.001);
    }

    @Test
    public void getY() throws Exception {
        //Assert default
        assertEquals("y value is not the given value in the constructor", y, point.getY(), 0.001);
    }

    @Test
    public void setY() throws Exception {
        //Assert default
        assertEquals("y value is not the given value in the constructor", y, point.getY(), 0.001);

        double newY = 60;

        //Change
        point.setY(newY);

        //Asset changed
        assertEquals("y value is not the given value in the setY method", newY, point.getY(), 0.001);
    }
}