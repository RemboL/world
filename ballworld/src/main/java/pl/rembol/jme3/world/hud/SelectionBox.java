package pl.rembol.jme3.world.hud;

import com.jme3.ui.Picture;
import pl.rembol.jme3.world.GameState;

public class SelectionBox {

    public SelectionBox(GameState gameState) {
        Picture frame = new Picture("Selection Box");
        frame.setImage(gameState.assetManager, "interface/selection_box.png", true);
        frame.move(0, 0, -2);
        frame.setWidth(200);
        frame.setHeight(200);
        gameState.guiNode.attachChild(frame);

    }
}
