package pl.rembol.jme3.world.input;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.input.state.InputStateManager;

@Component
public class CommandKeysListener implements ActionListener {

    @Autowired
    private InputStateManager inputStateManager;

    @Autowired
    private GameState gameState;

    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
        if (keyPressed) {
            inputStateManager.type(name);
        }
    }

    @PostConstruct
    public void registerInput() {
        bindKeyToCommand(gameState, InputStateManager.B, KeyInput.KEY_B);
        bindKeyToCommand(gameState, InputStateManager.C, KeyInput.KEY_C);
        bindKeyToCommand(gameState, InputStateManager.F, KeyInput.KEY_F);
        bindKeyToCommand(gameState, InputStateManager.H, KeyInput.KEY_H);
        bindKeyToCommand(gameState, InputStateManager.M, KeyInput.KEY_M);
        bindKeyToCommand(gameState, InputStateManager.R, KeyInput.KEY_R);
        bindKeyToCommand(gameState, InputStateManager.W, KeyInput.KEY_W);
        bindKeyToCommand(gameState, InputStateManager.T, KeyInput.KEY_T);
    }

    private void bindKeyToCommand(GameState gameState, String command,
            int key) {
        gameState.inputManager.addMapping(command, new KeyTrigger(key));
        gameState.inputManager.addListener(this, command);
    }

}
