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

    public static final String MOUSE_MOVE_UP = "Mouse_moveUp";

    public static final String MOUSE_MOVE_DOWN = "Mouse_moveDown";

    public static final String MOUSE_MOVE_LEFT = "Mouse_moveLeft";

    public static final String MOUSE_MOVE_RIGHT = "Mouse_moveRight";

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

        gameState.inputManager.addMapping(MOUSE_MOVE_UP,
                new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        gameState.inputManager.addListener(this, MOUSE_MOVE_UP);

        gameState.inputManager.addMapping(MOUSE_MOVE_DOWN,
                new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        gameState.inputManager.addListener(this, MOUSE_MOVE_DOWN);

        gameState.inputManager.addMapping(MOUSE_MOVE_LEFT,
                new MouseAxisTrigger(MouseInput.AXIS_X, false));
        gameState.inputManager.addListener(this, MOUSE_MOVE_LEFT);

        gameState.inputManager.addMapping(MOUSE_MOVE_RIGHT,
                new MouseAxisTrigger(MouseInput.AXIS_X, true));
        gameState.inputManager.addListener(this, MOUSE_MOVE_RIGHT);

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
