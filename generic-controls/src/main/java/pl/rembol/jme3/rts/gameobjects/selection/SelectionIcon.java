package pl.rembol.jme3.rts.gameobjects.selection;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gui.ClickablePicture;
import pl.rembol.jme3.rts.gui.model.ModelViewer;

public class SelectionIcon extends ClickablePicture {

    protected Selectable selectable;

    private SelectionIcon(GameState gameState, Selectable selectable) {
        super(gameState);
        picture.setHeight(32);
        picture.setWidth(32);
        this.selectable = selectable;
    }

    public SelectionIcon(GameState gameState, Selectable selectable, String iconName) {
        this(gameState, selectable);
        picture.setName(iconName);
        picture.setImage(gameState.assetManager, "interface/icons/" + iconName + ".png", true);
    }

    public SelectionIcon(GameState gameState, Selectable selectable, Spatial model) {
        this(gameState, selectable);
        updateWithModel(model);
    }

    @Override
    public void onClick() {
        gameState.selectionManager.select(selectable);
    }

    public void updateWithModel(Spatial model) {
        new ModelViewer(gameState, 32, 32).withBackgroundColor(new ColorRGBA(.2f, .2f, .2f, .2f)).withModel(model).updateViewer(picture);
    }
}