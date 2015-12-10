package pl.rembol.jme3.rts.gameobjects.selection;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gui.ClickablePicture;
import pl.rembol.jme3.rts.gui.model.ModelViewer;

public class SelectionIcon extends ClickablePicture {

    protected Selectable selectable;

    public SelectionIcon(GameState gameState, Selectable selectable, String iconName) {
        super(gameState, iconName);
        this.selectable = selectable;
        setImage(gameState.assetManager, "interface/icons/" + iconName + ".png", true);
    }

    public SelectionIcon(GameState gameState, Selectable selectable, Spatial model) {
        super(gameState);
        this.selectable = selectable;
        updateWithModel(model);
        setWidth(SIZE);
        setHeight(SIZE);
    }

    @Override
    public void onClick() {
        gameState.selectionManager.select(selectable);
    }

    public void updateWithModel(Spatial model) {
        new ModelViewer(gameState, 32, 32).withBackgroundColor(new ColorRGBA(.2f, .2f, .2f, .2f)).withModel(model).updateViewer(this);
    }
}