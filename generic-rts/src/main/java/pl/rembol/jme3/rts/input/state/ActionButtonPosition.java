package pl.rembol.jme3.rts.input.state;

public enum ActionButtonPosition {

    UPPER_LEFT(0, 0),
    UPPER_CENTER(1, 0),
    UPPER_RIGHT(2, 0),
    MIDDLE_LEFT(0, 1),
    MIDDLE_CENTER(1, 1),
    MIDDLE_RIGHT(2, 1),
    LOWER_LEFT(0, 2),
    LOWER_CENTER(1, 2),
    LOWER_RIGHT(2, 2);

    private int x;
    private int y;

    private ActionButtonPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
