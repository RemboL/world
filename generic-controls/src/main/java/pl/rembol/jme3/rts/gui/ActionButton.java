package pl.rembol.jme3.rts.gui;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.input.state.Command;

public class ActionButton extends ClickablePicture {

    public ActionButton(GameState gameState, Command command) {
        super(gameState);

        picture.setHeight(32);
        picture.setWidth(32);
        picture.setName(command.getIconName());
        picture.setImage(gameState.assetManager, "interface/icons/" + command.getIconName() + ".png", true);
        move(60 * command.getPositionX(), -60 * command.getPositionY(), 0);

        addOnClickListener(() -> gameState.inputStateManager.type(command.getKey()));
    }

}
