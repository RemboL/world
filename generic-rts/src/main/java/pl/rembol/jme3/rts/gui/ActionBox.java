package pl.rembol.jme3.rts.gui;

import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.events.SelectionChangedEvent;
import pl.rembol.jme3.rts.input.state.Command;

public class ActionBox {

    private Node buttonsNode;

    private RtsGameState gameState;

    public ActionBox(RtsGameState gameState) {
        this.gameState = gameState;

        buttonsNode = new Node("Action buttons");
        buttonsNode.move(gameState.settings.getWidth() - 172, 146, 0);
        gameState.guiNode.attachChild(buttonsNode);

        gameState.eventManager.addListener(SelectionChangedEvent.class, selectionChangedEvent -> updateActionButtons());
    }

    public void initFrame(String themeName) {
        Picture frame = new Picture("Action Box");
        frame.setImage(gameState.assetManager, "interface/" + themeName + "/action_box.png", true);
        frame.move(gameState.settings.getWidth() - 200, 0, -2);
        frame.setWidth(200);
        frame.setHeight(200);
        gameState.guiNode.attachChild(frame);
    }

    public void updateActionButtons() {
        clearButtons();
        gameState.inputStateManager
                .getAvailableCommands()
                .forEach(this::createActionButton);
    }

    private void createActionButton(Command command) {
        Node button = new ActionButton(gameState, command);
        buttonsNode.attachChild(button);
    }

    private void clearButtons() {
        buttonsNode.detachAllChildren();
    }

}
