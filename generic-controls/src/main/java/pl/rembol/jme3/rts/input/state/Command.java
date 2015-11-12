package pl.rembol.jme3.rts.input.state;

public class Command {

    private ActionButtonPosition position;
    private String iconName;
    private int keyInput;

    public Command(ActionButtonPosition position, String iconName,
                    int keyInput) {
        this.position = position;
        this.iconName = iconName;
        this.keyInput = keyInput;
    }

    public int getPositionX() {
        return position.getX();
    }

    public int getPositionY() {
        return position.getY();
    }

    public String getIconName() {
        return iconName;
    }

    public int getKey() {
        return keyInput;
    }
}
