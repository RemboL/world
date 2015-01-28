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
		inputStateManager.issueCommand(name);
	}

	public void registerInput(InputManager inputManager) {
		inputManager.addMapping(InputStateManager.M, new KeyTrigger(
				KeyInput.KEY_M));
		inputManager.addListener(this, InputStateManager.M);

		inputManager.addMapping(InputStateManager.F, new KeyTrigger(
				KeyInput.KEY_F));
		inputManager.addListener(this, InputStateManager.F);

		inputManager.addMapping(InputStateManager.B, new KeyTrigger(
				KeyInput.KEY_B));
		inputManager.addListener(this, InputStateManager.B);
	}

}
