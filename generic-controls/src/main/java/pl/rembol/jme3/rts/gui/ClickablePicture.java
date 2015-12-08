package pl.rembol.jme3.rts.gui;

import com.jme3.math.Vector2f;
import com.jme3.ui.Picture;
import pl.rembol.jme3.rts.GameState;

abstract public class ClickablePicture extends Picture implements Clickable {

    protected static final int SIZE = 32;

    protected final GameState gameState;

    public ClickablePicture(GameState gameState) {
        super("");
        this.gameState = gameState;
    }

    public ClickablePicture(GameState gameState, String iconName) {
        super(iconName);
        this.gameState = gameState;
        setImage(gameState.assetManager, "interface/icons/" + iconName + ".png", true);
        setWidth(SIZE);
        setHeight(SIZE);
    }

    @Override
    public boolean isClicked(Vector2f cursorPosition) {
        return getWorldTranslation().x <= cursorPosition.x &&
                getWorldTranslation().x + getWorldScale().x >= cursorPosition.x &&
                getWorldTranslation().y <= cursorPosition.y &&
                getWorldTranslation().y + getWorldScale().y >= cursorPosition.y;
    }
}
