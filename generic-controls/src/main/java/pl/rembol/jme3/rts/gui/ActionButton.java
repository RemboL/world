package pl.rembol.jme3.rts.gui;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.input.state.Command;

public class ActionButton extends ClickablePicture {

    private Command command;

    public ActionButton(GameState gameState, Command command) {
        super(gameState, command.getIconName());
        this.command = command;
        move(60 * command.getPositionX(), -60 * command.getPositionY(), 0);
    }

    @Override
    public void onClick() {
        gameState.inputStateManager.type(command.getKey());
    }
}
