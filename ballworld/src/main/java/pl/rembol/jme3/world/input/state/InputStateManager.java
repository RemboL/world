package pl.rembol.jme3.world.input.state;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.rts.gameobjects.order.WithSilhouette;
import pl.rembol.jme3.rts.input.state.Command;
import pl.rembol.jme3.rts.input.state.InputState;
import pl.rembol.jme3.rts.input.state.StateTransition;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;
import pl.rembol.jme3.rts.gameobjects.order.DefaultActionOrder;
import pl.rembol.jme3.rts.gameobjects.order.Order;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.world.GameState;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InputStateManager {

    public static final String RIGHT_CLICK = "command_rightClick";

    public static final String LEFT_CLICK = "command_leftClick";

    public static final String MOUSE_MOVE = "command_mouseMove";

    private InputState currentState = InputState.DEFAULT;

    private Order<?> currentOrder = null;

    private GameState gameState;

    public InputStateManager(GameState gameState) {
        this.gameState = gameState;
    }

    public void click(String command, WithNode target) {
        if (currentState == InputState.ISSUE_ORDER
                || currentState == InputState.ISSUE_BUILD_ORDER) {
            switch (command) {
                case InputStateManager.LEFT_CLICK:
                    if (currentOrder instanceof WithSilhouette
                            && ((WithSilhouette) currentOrder).snapTargetPositionToGrid()) {
                        Vector3f location = gameState.buildingSilhouetteManager.getSilhouettePosition();
                        if (location != null) {
                            performOrder(new Vector2f(location.x, location.z));
                        } else {
                            cancelOrder();
                        }
                    } else {
                        performOrder(target);
                    }
                    break;
                case InputStateManager.RIGHT_CLICK:
                    cancelOrder();
                    break;
            }
        } else if (currentState == InputState.DEFAULT) {
            switch (command) {
                case InputStateManager.LEFT_CLICK:
                    gameState.selectionManager.select(Selectable.class.cast(target));
                    break;
                case InputStateManager.RIGHT_CLICK:
                    new DefaultActionOrder(gameState, gameState.selectionManager.getSelected()).perform(target);
                    break;
            }
        }

        gameState.actionBox.updateActionButtons();
        gameState.buildingSilhouetteManager.removeSilhouette();
    }

    public void click(String command, Vector2f target) {
        if (currentState == InputState.ISSUE_ORDER || currentState == InputState.ISSUE_BUILD_ORDER) {
            switch (command) {
                case InputStateManager.LEFT_CLICK:
                    if (currentOrder instanceof WithSilhouette
                            && ((WithSilhouette) currentOrder).snapTargetPositionToGrid()) {
                        Vector3f location = gameState.buildingSilhouetteManager.getSilhouettePosition();
                        if (location != null) {
                            performOrder(new Vector2f(location.x, location.z));
                        } else {
                            cancelOrder();
                        }
                    } else {
                        performOrder(target);
                    }
                    break;
                case InputStateManager.RIGHT_CLICK:
                    cancelOrder();
                    break;
            }
        } else if (currentState == InputState.DEFAULT) {
            switch (command) {
                case InputStateManager.LEFT_CLICK:
                    break;
                case InputStateManager.RIGHT_CLICK:
                    new DefaultActionOrder(gameState, gameState.selectionManager.getSelected()).perform(target);
                    break;
            }
        }

        gameState.actionBox.updateActionButtons();
        gameState.buildingSilhouetteManager.removeSilhouette();
    }

    public void type(int key) {
        getTransitionAndChangeState(key);

        gameState.actionBox.updateActionButtons();
    }

    public void cancelOrder() {
        currentOrder = null;
        currentState = InputState.DEFAULT;

        gameState.actionBox.updateActionButtons();
        gameState.buildingSilhouetteManager.removeSilhouette();
    }

    private void performOrder(WithNode target) {
        currentOrder.perform(target);
        currentOrder = null;
        currentState = InputState.DEFAULT;

    }

    private void performOrder(Vector2f target) {
        currentOrder.perform(target);
        currentOrder = null;
        currentState = InputState.DEFAULT;

    }

    private Optional<StateTransition> getTransitionAndChangeState(int key) {
        Optional<StateTransition> transition = gameState.stateTransitionsRegistry.match(currentState,
                gameState.selectionManager.getSelected(), key);

        if (transition.isPresent()) {
            currentState = transition.get().getTargetState();
            if (currentState == InputState.ISSUE_ORDER
                    || currentState == InputState.ISSUE_BUILD_ORDER) {
                currentOrder = transition.get().getOrder(gameState);
                gameState.buildingSilhouetteManager.createSilhouette(currentOrder);
            } else if (currentState == InputState.ISSUE_ORDER_IMMEDIATELY) {
                currentOrder = null;
                transition.get().getOrder(gameState).perform(Selectable.class.cast(null));
                currentState = InputState.DEFAULT;
            } else {
                currentOrder = null;
            }
        }

        return transition;
    }

    public List<Command> getAvailableCommands() {
        return gameState.stateTransitionsRegistry
                .match(currentState, gameState.selectionManager.getSelected())
                .stream().map(StateTransition::getCommandButton)
                .collect(Collectors.toList());
    }

}
