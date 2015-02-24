package pl.rembol.jme3.input;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.rembol.jme3.input.state.InputStateManager;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

@Component
public class CommandKeysListener implements ActionListener {

	@Autowired
	private InputStateManager inputStateManager;

	@Autowired
	private InputManager inputManager;

	@Override
	public void onAction(String name, boolean keyPressed, float tpf) {
		inputStateManager.type(name);
	}

	@PostConstruct
	public void registerInput() {
		bindKeyToCommand(inputManager, InputStateManager.B, KeyInput.KEY_B);
		bindKeyToCommand(inputManager, InputStateManager.C, KeyInput.KEY_C);
		bindKeyToCommand(inputManager, InputStateManager.F, KeyInput.KEY_F);
		bindKeyToCommand(inputManager, InputStateManager.H, KeyInput.KEY_H);
		bindKeyToCommand(inputManager, InputStateManager.M, KeyInput.KEY_M);
		bindKeyToCommand(inputManager, InputStateManager.R, KeyInput.KEY_R);
		bindKeyToCommand(inputManager, InputStateManager.W, KeyInput.KEY_W);
	}

	private void bindKeyToCommand(InputManager inputManager, String command,
			int key) {
		inputManager.addMapping(command, new KeyTrigger(key));
		inputManager.addListener(this, command);
	}

}
