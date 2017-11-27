package logic;

public class Gamerule {
    private Gamerules gamerules;


    public Gamerules getGamerules()
    {
        return gamerules;
    }

    public int getValue()
    {
        return value;
    }

    private int value;

    public Gamerule(Gamerules gamerules) {
        this.gamerules = gamerules;
        this.value = 0;
    }
    //test
    //test

    public void setValue(int value) {
        this.value = value;
    }
}
