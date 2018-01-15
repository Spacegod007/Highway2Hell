
import logic.Score;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ScoreTest {

    private final Score s = new Score("", 0);

    @Before
    public void setUp() throws Exception {
        s.setScore(10d);
        s.setName("Test");
    }

    @Test
    public void getName() throws Exception {
        //Assert default
        assertEquals("Test", s.getName());

        //Change
        s.setName("Test2");

        //Asset changed
        assertEquals("Test2", s.getName());
    }

    @Test
    public void setName() throws Exception {
        //Assert default
        assertEquals("Test", s.getName());

        //Change
        s.setName("Test2");

        //Asset changed
        assertEquals("Test2", s.getName());
    }

}