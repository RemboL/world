package pl.rembol.jme3.world.hud.status;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.selection.SelectionIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatusBar {

    private static final int ICON_ROW_SIZE = 9;
    private static final int ICON_LINES = 3;
    private List<SelectionIcon> selectionIcons = new ArrayList<>();

    private Vector2f framePosition;

    private GameState gameState;

    public StatusBar(GameState gameState) {
        this.gameState = gameState;

        framePosition = new Vector2f(gameState.settings.getWidth() / 2 - 200, 0);

        Picture frame = new Picture("Status Bar");
        frame.setImage(gameState.assetManager, "interface/status_bar.png", true);
        frame.move(framePosition.x, framePosition.y, -2);
        frame.setWidth(400);
        frame.setHeight(120);
        gameState.guiNode.attachChild(frame);

        gameState.guiNode.attachChild(statusDetails);
        statusDetails.move(framePosition.x, framePosition.y, 0);
    }

    private Node statusDetails = new Node("status details connector");

    public void clearIcons() {
        selectionIcons.forEach(gameState.guiNode::detachChild);

        selectionIcons.clear();
    }

    public void clear() {
        clearIcons();
        statusDetails.detachAllChildren();
    }

    public void setIcons(List<Selectable> selectables) {
        clear();

        int index = 0;
        for (Selectable selectable : selectables.stream()
                .limit(ICON_LINES * ICON_ROW_SIZE).collect(Collectors.toList())) {
            SelectionIcon icon = selectable.getIcon();
            icon.setLocalTranslation(framePosition.x + 40
                    + (index % ICON_ROW_SIZE) * 36, framePosition.y + 44
                    + (1 - (index / ICON_ROW_SIZE)) * 32, 1);
            gameState.guiNode.attachChild(icon);
            selectionIcons.add(icon);

            index++;
        }

    }

    public List<SelectionIcon> getSelectionIcons() {
        return selectionIcons;
    }

    public void setStatusDetails(Node statusDetails) {
        clear();

        this.statusDetails.attachChild(statusDetails);

    }

}
