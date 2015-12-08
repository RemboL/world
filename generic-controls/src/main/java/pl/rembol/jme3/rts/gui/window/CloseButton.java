package pl.rembol.jme3.rts.gui.window;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gui.ClickablePicture;

public class CloseButton extends ClickablePicture {

    private final Window window;

    public CloseButton(GameState gameState, Window window) {
        super(gameState);
        this.setName("close button");
        setImage(gameState.assetManager, "interface/" + gameState.themeName() + "/close_button.png", true);
        setWidth(SIZE);
        setHeight(SIZE);
        this.window = window;
    }

    @Override
    public void onClick() {
        window.close();
    }
}
