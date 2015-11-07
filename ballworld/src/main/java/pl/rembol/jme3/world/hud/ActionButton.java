package pl.rembol.jme3.world.hud;

import pl.rembol.jme3.rts.gui.ClickablePicture;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.input.state.Command;

public class ActionButton extends ClickablePicture {

    public static final int SIZE = 32;

    private Command command;

    public ActionButton(GameState gameState, Command command) {
        super(gameState, command.getIconName());
        this.command = command;
        move(60 * command.getPositionX(), -60 * command.getPositionY(), 0);
    }

    @Override
    public void onClick() {
        // TODO FIXME
        pl.rembol.jme3.world.GameState.class.cast(gameState).inputStateManager.type(command.getCommandKey());
    }
}
