

import org.junit.Before;
import org.junit.Test;
import logic.*;
import static org.junit.Assert.*;

public class GameruleTest {

    private final Gamerule GR = new Gamerule(Gamerules.GAMERULE1);

    @Before
    public void setUp() throws Exception {
        GR.setValue(10);
    }

    @Test
    public void getGamerules() throws Exception {
        //Assert default
        assertEquals(Gamerules.GAMERULE1, GR.getGamerules());
    }

    @Test
    public void getValue() throws Exception {
        assertEquals(10, GR.getValue());
    }

    @Test
    public void setValue() throws Exception {
        GR.setValue(20);
        assertEquals(20, GR.getValue());
    }

}