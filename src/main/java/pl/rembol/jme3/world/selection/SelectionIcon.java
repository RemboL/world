package pl.rembol.jme3.world.selection;

import com.jme3.asset.AssetManager;
import com.jme3.ui.Picture;

public class SelectionIcon extends Picture {

    public static final int SIZE = 32;
    private Selectable selectable;

    public SelectionIcon(Selectable selectable, String iconName,
            AssetManager assetManager) {
        super(iconName);
        this.selectable = selectable;
        setImage(assetManager, "interface/icons/" + iconName + ".png", true);
        setWidth(SIZE);
        setHeight(SIZE);
    }

    public Selectable getSelectable() {
        return selectable;
    }

}