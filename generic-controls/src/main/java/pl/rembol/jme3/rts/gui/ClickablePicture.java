package pl.rembol.jme3.rts.gui;

import com.jme3.ui.Picture;
import pl.rembol.jme3.rts.GameState;

abstract public class ClickablePicture extends PictureNode {

    protected final GameState gameState;

    public ClickablePicture(GameState gameState, Picture picture) {
        super(picture);
        this.gameState = gameState;
    }

    public ClickablePicture(GameState gameState) {
        this(gameState, new Picture(""));
    }


}
