package pl.rembol.jme3.rts.gui.window;

import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gui.ClickablePicture;

public class CloseButton extends ClickablePicture {

    private final Window window;

    public CloseButton(RtsGameState gameState, Window window) {
        super(gameState);
        this.setName("close button");
        picture.setImage(gameState.assetManager, "interface/" + gameState.themeName() + "/close_button.png", true);
        picture.setWidth(32);
        picture.setHeight(32);
        this.window = window;
    }

    @Override
    public void onClick() {
        window.close();
    }
}
