package pl.rembol.jme3.game.input;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import pl.rembol.jme3.game.GenericGameState;

public class KeyInputManager implements ActionListener, AnalogListener {
    public static final String A = "Key_A";
    public static final String D = "Key_D";
    public static final String E = "Key_E";
    public static final String M = "Key_M";
    public static final String O = "Key_O";
    public static final String Q = "Key_Q";
    public static final String S = "Key_S";
    public static final String W = "Key_W";
    public static final String SPACE = "Key_Space";
    public static final String TAB = "Key_Tab";
    private GenericGameState gameState;

    public KeyInputManager(GenericGameState gameState) {
        this.gameState = gameState;

        bindKey(A, KeyInput.KEY_A);
        bindKey(D, KeyInput.KEY_D);
        bindKey(E, KeyInput.KEY_E);
        bindKey(M, KeyInput.KEY_M);
        bindKey(O, KeyInput.KEY_O);
        bindKey(Q, KeyInput.KEY_Q);
        bindKey(S, KeyInput.KEY_S);
        bindKey(W, KeyInput.KEY_W);
        bindKey(SPACE, KeyInput.KEY_SPACE);
        bindKey(TAB, KeyInput.KEY_TAB);
    }

    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
        if (gameState.windowManager.getTopWindow().isPresent()) {
            gameState.windowManager.getTopWindow().get().onAction(name, keyPressed, tpf);
        } else {
            gameState.defaultInputListener.onAction(name, keyPressed, tpf);
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (gameState.windowManager.getTopWindow().isPresent()) {
            gameState.windowManager.getTopWindow().get().onAnalog(name, value, tpf);
        } else {
            gameState.defaultInputListener.onAnalog(name, value, tpf);
        }
    }

    private void bindKey(String command, int key) {
        gameState.inputManager.addMapping(command, new KeyTrigger(key));
        gameState.inputManager.addListener(this, command);
    }
}
