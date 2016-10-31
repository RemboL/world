package pl.rembol.jme3.game.input;

import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import pl.rembol.jme3.game.GenericGameState;

public class MouseInputManager implements ActionListener, AnalogListener {

    public static final String LEFT_CLICK = "Mouse_leftClick";

    public static final String RIGHT_CLICK = "Mouse_rightClick";

    public static final String MOUSE_MOVE = "Mouse_move";
    
    public static final String MOUSE_SCROLL_UP = "Mouse_scrollUp";

    public static final String MOUSE_SCROLL_DOWN = "Mouse_scrollDown";
    
    private GenericGameState gameState;

    public MouseInputManager(GenericGameState gameState) {
        this.gameState = gameState;

        gameState.inputManager.addMapping(LEFT_CLICK,
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        gameState.inputManager.addListener(this, LEFT_CLICK);

        gameState.inputManager.addMapping(RIGHT_CLICK,
                new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        gameState.inputManager.addListener(this, RIGHT_CLICK);

        gameState.inputManager.addMapping(MOUSE_MOVE,
                new MouseAxisTrigger(MouseInput.AXIS_X, false),
                new MouseAxisTrigger(MouseInput.AXIS_X, true),
                new MouseAxisTrigger(MouseInput.AXIS_Y, false),
                new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        gameState.inputManager.addListener(this, MOUSE_MOVE);

        gameState.inputManager.addMapping(MOUSE_SCROLL_UP, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        gameState.inputManager.addMapping(MOUSE_SCROLL_DOWN, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        gameState.inputManager.addListener(this, MOUSE_SCROLL_UP);
        gameState.inputManager.addListener(this, MOUSE_SCROLL_DOWN);

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
}
