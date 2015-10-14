package pl.rembol.jme3.world.selection;

import com.jme3.ui.Picture;
import pl.rembol.jme3.world.GameState;

public class SelectionIcon extends Picture {

    public static final int SIZE = 32;
    private Selectable selectable;

    public SelectionIcon(Selectable selectable, String iconName,
            GameState gameState) {
        super(iconName);
        this.selectable = selectable;
        setImage(gameState.assetManager, "interface/icons/" + iconName + ".png", true);
        setWidth(SIZE);
        setHeight(SIZE);
    }

    public Selectable getSelectable() {
        return selectable;
    }

}