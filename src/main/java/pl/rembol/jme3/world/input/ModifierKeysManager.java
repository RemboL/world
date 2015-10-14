package pl.rembol.jme3.world.input;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import pl.rembol.jme3.world.GameState;

@Component
public class ModifierKeysManager implements ActionListener {

	private static final String SHIFT = "modifier_shift";

	private static final String CONTROL = "modifier_control";

	private boolean shiftPressed = false;

	private boolean controlPressed = false;

	@Autowired
	private GameState gameState;

	@Override
	public void onAction(String name, boolean keyPressed, float tpf) {

		if (SHIFT.equals(name)) {
			shiftPressed = keyPressed;
		}

		if (CONTROL.equals(name)) {
			controlPressed = keyPressed;
		}

	}

	@PostConstruct
	public void registerInput() {
		gameState.inputManager.addMapping(SHIFT, new KeyTrigger(KeyInput.KEY_RSHIFT),
				new KeyTrigger(KeyInput.KEY_LSHIFT));
		gameState.inputManager.addListener(this, SHIFT);

		gameState.inputManager.addMapping(CONTROL, new KeyTrigger(KeyInput.KEY_RCONTROL),
				new KeyTrigger(KeyInput.KEY_LCONTROL));
		gameState.inputManager.addListener(this, CONTROL);
	}

	public boolean isShiftPressed() {
		return shiftPressed;
	}

	public boolean isControlPressed() {
		return controlPressed;
	}

}
