package pl.rembol.jme3.world.hud;

import com.jme3.asset.AssetManager;
import com.jme3.ui.Picture;
import pl.rembol.jme3.world.input.state.Command;

public class ActionButton extends Picture {

	public static final int SIZE = 32;

	private Command command;

	public ActionButton(Command command, AssetManager assetManager) {
		super(command.getIconName());
		this.command = command;
		setImage(assetManager, "interface/icons/" + command.getIconName()
				+ ".png", true);
		move(60 * command.getPositionX(), -60 * command.getPositionY(), 0);
		setWidth(SIZE);
		setHeight(SIZE);
	}

	public String getCommandKey() {
		return command.getCommandKey();
	}

}
