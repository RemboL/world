package pl.rembol.jme3.world.hud;

import com.jme3.ui.Picture;
import pl.rembol.jme3.world.GameState;

public class InventoryIcon extends Picture {

    public static final int SIZE = 32;

    public InventoryIcon(String iconName, GameState gameState) {
        super(iconName);
        setImage(gameState.assetManager, "interface/icons/" + iconName + ".png", true);
        setWidth(SIZE);
        setHeight(SIZE);
    }

}