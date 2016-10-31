package pl.rembol.jme3.rts.gui;

import com.jme3.ui.Picture;
import pl.rembol.jme3.rts.RtsGameState;

abstract public class ClickablePicture extends PictureNode {

    protected final RtsGameState gameState;

    public ClickablePicture(RtsGameState gameState, Picture picture) {
        super(picture);
        this.gameState = gameState;
    }

    public ClickablePicture(RtsGameState gameState) {
        this(gameState, new Picture(""));
    }


}
