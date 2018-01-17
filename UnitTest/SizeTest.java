import logic.game.Size;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SizeTest {

    private double width;
    private double height;

    private Size size;

    @Before
    public void setUp() throws Exception
    {
        width = 1;
        height = 2;

        size = new Size(width, height);
    }

    @Test
    public void classConstruction() throws Exception
    {
        try
        {
            new Size(0, 1);
            fail("Width should always be more than 0");
        }
        catch (IndexOutOfBoundsException ignored)
        { }

        try
        {
            new Size(1, 0);
            fail("Height should always be more than 0");
        }
        catch (IndexOutOfBoundsException ignored)
        { }

        try
        {
            new Size(-1, 1);
            fail("Width should always be more than 0");
        }
        catch (IndexOutOfBoundsException ignored)
        { }

        try
        {
            new Size(1, -1);
            fail("Height should always be more than 0");
        }
        catch (IndexOutOfBoundsException ignored)
        { }
    }

    @Test
    public void getWidth() throws Exception {
        //Assert default
        assertEquals("Width is not the value set in the constructor", width, size.getWidth(), 0.001);
    }

    @Test
    public void setWidth() throws Exception {
        //Assert default
        assertEquals("Width is not the value set in the constructor", width, size.getWidth(), 0.001);

        double newWidth = 5;

        //Change
        size.setWidth(newWidth);

        //Asset changed
        assertEquals("Width is not the value set by the setWidth method", newWidth, size.getWidth(), 0.001);
    }

    @Test
    public void getHeight() throws Exception {
        //Assert default
        assertEquals("Height is not the value set in the constructor",height, size.getHeight(), 0.001);
    }

    @Test
    public void setHeight() throws Exception {
        //Assert default
        assertEquals("Height is not the value set in the constructor",height, size.getHeight(), 0.001);

        double newHeight = 6;

        //Change
        size.setHeight(newHeight);

        //Asset changed
        assertEquals("Height is not the value set by the setHeight method", newHeight, size.getHeight(), 0.001);
    }
}