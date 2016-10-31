package pl.rembol.jme3.rts.input;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

public class ModifierKeysManager implements ActionListener {

    private static final String SHIFT = "modifier_shift";

    private static final String CONTROL = "modifier_control";

    private boolean shiftPressed = false;

    private boolean controlPressed = false;

    public ModifierKeysManager(SimpleApplication simpleApplication) {
        simpleApplication.getInputManager().addMapping(SHIFT, new KeyTrigger(KeyInput.KEY_RSHIFT),
                new KeyTrigger(KeyInput.KEY_LSHIFT));
        simpleApplication.getInputManager().addListener(this, SHIFT);

        simpleApplication.getInputManager().addMapping(CONTROL, new KeyTrigger(KeyInput.KEY_RCONTROL),
                new KeyTrigger(KeyInput.KEY_LCONTROL));
        simpleApplication.getInputManager().addListener(this, CONTROL);
    }

    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {

        if (SHIFT.equals(name)) {
            shiftPressed = keyPressed;
        }

        if (CONTROL.equals(name)) {
            controlPressed = keyPressed;
        }

    }

    public boolean isShiftPressed() {
        return shiftPressed;
    }

    public boolean isControlPressed() {
        return controlPressed;
    }

}
