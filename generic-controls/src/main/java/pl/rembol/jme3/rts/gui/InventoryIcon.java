package pl.rembol.jme3.rts.gui;

import com.jme3.ui.Picture;
import pl.rembol.jme3.rts.GameState;

public class InventoryIcon extends Picture {

    public static final int SIZE = 32;

    public InventoryIcon(GameState gameState, String iconName) {
        super(iconName);
        setImage(gameState.assetManager, "interface/icons/" + iconName + ".png", true);
        setWidth(SIZE);
        setHeight(SIZE);
    }

}