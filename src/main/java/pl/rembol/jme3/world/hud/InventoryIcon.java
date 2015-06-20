package pl.rembol.jme3.world.hud;

import com.jme3.asset.AssetManager;
import com.jme3.ui.Picture;

public class InventoryIcon extends Picture {

    public static final int SIZE = 32;

    public InventoryIcon(String iconName, AssetManager assetManager) {
        super(iconName);
        setImage(assetManager, "interface/icons/" + iconName + ".png", true);
        setWidth(SIZE);
        setHeight(SIZE);
    }

}