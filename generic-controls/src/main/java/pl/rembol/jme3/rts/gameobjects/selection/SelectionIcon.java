package pl.rembol.jme3.rts.gameobjects.selection;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gui.ClickablePicture;

public class SelectionIcon extends ClickablePicture {

    protected Selectable selectable;

    public SelectionIcon(GameState gameState, Selectable selectable, String iconName) {
        super(gameState, iconName);
        this.selectable = selectable;
        setImage(gameState.assetManager, "interface/icons/" + iconName + ".png", true);
    }

    @Override
    public void onClick() {
        // TODO FIXME
//        gameState.selectionManager.select(selectable);
    }
}