import logic.game.Size;
import org.junit.Test;

import static org.junit.Assert.*;

public class SizeTest {

    private final Size s = new Size(1, 1);


    @Test
    public void getWidth() throws Exception {
        //Assert default
        assertEquals(1d, s.getWidth(), 0.001);

        //Change
        s.setWidth(10d);

        //Asset changed
        assertEquals(10d, s.getWidth(), 0.001);
    }

    @Test
    public void setWidth() throws Exception {
        //Assert default
        assertEquals(1d, s.getWidth(), 0.001);

        //Change
        s.setWidth(10d);

        //Asset changed
        assertEquals(10d, s.getWidth(), 0.001);
    }

    @Test
    public void getHeight() throws Exception {
        //Assert default
        assertEquals(1d, s.getHeight(), 0.001);

        //Change
        s.setHeight(10d);

        //Asset changed
        assertEquals(10d, s.getHeight(), 0.001);
    }

    @Test
    public void setHeight() throws Exception {
        //Assert default
        assertEquals(1d, s.getHeight(), 0.001);

        //Change
        s.setHeight(10d);

        //Asset changed
        assertEquals(10d, s.getHeight(), 0.001);
    }

}