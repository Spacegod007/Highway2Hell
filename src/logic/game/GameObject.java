package logic.game;

public abstract class GameObject {
    private Point anchor;

    public GameObject(Point anchor) {
        this.anchor = anchor;
    }

    public Point getAnchor() {
        return anchor;
    }

    public void setAnchor(Point anchor) {
        this.anchor = anchor;
    }
}
