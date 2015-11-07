package pl.rembol.jme3.world.hud;

import java.util.List;

import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.input.state.Command;

public class ActionBox {

    private Node buttonsNode;

    private GameState gameState;

    public ActionBox(GameState gameState) {
        this.gameState = gameState;

        Picture frame = new Picture("Action Box");
        frame.setImage(gameState.assetManager, "interface/action_box.png", true);
        frame.move(gameState.settings.getWidth() - 200, 0, -2);
        frame.setWidth(200);
        frame.setHeight(200);
        gameState.guiNode.attachChild(frame);

        buttonsNode = new Node("Action buttons");
        buttonsNode.move(gameState.settings.getWidth() - 172, 146, 0);
        gameState.guiNode.attachChild(buttonsNode);

    }

    public void updateActionButtons() {
        clearButtons();
        List<Command> availableCommands = gameState.inputStateManager
                .getAvailableCommands();
        availableCommands.forEach(this::createActionButton);
    }

    private void createActionButton(Command command) {
        Picture button = new ActionButton(gameState, command);
        buttonsNode.attachChild(button);
    }

    private void clearButtons() {
        buttonsNode.detachAllChildren();
    }

}
