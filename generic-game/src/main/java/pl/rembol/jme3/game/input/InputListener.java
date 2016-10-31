package pl.rembol.jme3.game.input;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import pl.rembol.jme3.game.GenericGameState;

public abstract class InputListener<GS extends GenericGameState> implements AnalogListener, ActionListener {

    protected final GS gameState;

    public InputListener(GS gameState) {
        this.gameState = gameState;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
    }
}
