package logic.game;

import java.util.concurrent.ThreadLocalRandom;

public class ObstacleObject extends GameObject {
    private int width;
    private int height;

    public ObstacleObject(int width, int height) {
        super(new Point(ThreadLocalRandom.current().nextInt(0, 1150  + 1), ThreadLocalRandom.current().nextInt(-500, 1)));
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
