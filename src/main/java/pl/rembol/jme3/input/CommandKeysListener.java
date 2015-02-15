package pl.rembol.jme3.input;

import pl.rembol.jme3.input.state.InputStateManager;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

public class CommandKeysListener implements ActionListener {

	private InputStateManager inputStateManager;

	public CommandKeysListener(InputStateManager inputStateManager) {
		this.inputStateManager = inputStateManager;
	}

	@Override
	public void onAction(String name, boolean keyPressed, float tpf) {
		inputStateManager.type(name);
	}

	public void registerInput(InputManager inputManager) {
		bindKeyToCommand(inputManager, InputStateManager.B, KeyInput.KEY_B);
		bindKeyToCommand(inputManager, InputStateManager.C, KeyInput.KEY_C);
		bindKeyToCommand(inputManager, InputStateManager.F, KeyInput.KEY_F);
		bindKeyToCommand(inputManager, InputStateManager.M, KeyInput.KEY_M);
	}

	private void bindKeyToCommand(InputManager inputManager, String command,
			int key) {
		inputManager.addMapping(command, new KeyTrigger(key));
		inputManager.addListener(this, command);
	}

}
