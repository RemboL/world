package pl.rembol.jme3.rts.input;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import pl.rembol.jme3.rts.GameState;

public class CommandKeysListener implements ActionListener {

    private static final String COMMAND_KEY_PREFIX = "command_key_";
    private GameState gameState;

    public CommandKeysListener(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
        if (!gameState.windowManager.getTopWindow().isPresent()) {
            if (keyPressed) {
                if (name.matches(COMMAND_KEY_PREFIX + "\\d+")) {
                    gameState.inputStateManager.type(Integer.valueOf(name.replaceAll(COMMAND_KEY_PREFIX, "")));
                }
            }
        }
    }


    public void bindCommandKey(int key) {
        gameState.inputManager.addMapping(COMMAND_KEY_PREFIX + key, new KeyTrigger(key));
        gameState.inputManager.addListener(this, COMMAND_KEY_PREFIX + key);
    }

}
