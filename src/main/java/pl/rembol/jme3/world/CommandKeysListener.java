package pl.rembol.jme3.world;

import java.util.Arrays;
import java.util.List;

import com.jme3.input.controls.ActionListener;

public class CommandKeysListener implements ActionListener {

	private static final List<String> COMMANDS = Arrays.asList("move",
			"flatten");

	@Override
	public void onAction(String name, boolean keyPressed, float tpf) {
		if (COMMANDS.contains(name) && !keyPressed) {
			GameState.get().setCommand(name);
		}

	}

}
