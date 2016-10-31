package pl.rembol.jme3.rts.gui;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import pl.rembol.jme3.rts.RtsGameState;

public class ClickablePictureWrapper extends Node implements Clickable {

    @FunctionalInterface
    public interface Callback {
        void call();
    }

    protected final RtsGameState gameState;

    protected final Picture picture;

    protected final Callback callback;

    public ClickablePictureWrapper(RtsGameState gameState, Picture picture, Callback callback) {
        super(picture.getName());
        this.gameState = gameState;
        this.picture = picture;
        this.callback = callback;
        attachChild(picture);
    }

    @Override
    public void onClick() {
        callback.call();
    }

    @Override
    public boolean isClicked(Vector2f cursorPosition) {
        return getWorldTranslation().x <= cursorPosition.x &&
                getWorldTranslation().x + picture.getWorldScale().x >= cursorPosition.x &&
                getWorldTranslation().y <= cursorPosition.y &&
                getWorldTranslation().y + picture.getWorldScale().y >= cursorPosition.y;
    }
}
