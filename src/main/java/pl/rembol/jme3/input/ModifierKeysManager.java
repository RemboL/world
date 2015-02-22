package pl.rembol.jme3.input;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

@Component
public class ModifierKeysManager implements ActionListener {

	private static final String SHIFT = "modifier_shift";

	private static final String CONTROL = "modifier_control";

	private boolean shiftPressed = false;

	private boolean controlPressed = false;

	@Autowired
	private InputManager inputManager;

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
		inputManager.addMapping(SHIFT, new KeyTrigger(KeyInput.KEY_RSHIFT),
				new KeyTrigger(KeyInput.KEY_LSHIFT));
		inputManager.addListener(this, SHIFT);

		inputManager.addMapping(CONTROL, new KeyTrigger(KeyInput.KEY_RCONTROL),
				new KeyTrigger(KeyInput.KEY_LCONTROL));
		inputManager.addListener(this, CONTROL);
	}

	public boolean isShiftPressed() {
		return shiftPressed;
	}

	public boolean isControlPressed() {
		return controlPressed;
	}

}
