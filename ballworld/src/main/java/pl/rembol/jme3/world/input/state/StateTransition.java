package pl.rembol.jme3.world.input.state;

import pl.rembol.jme3.rts.unit.order.Order;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.order.OrderProducer;

public class StateTransition {

    private InputState currentState;

    private String commandKey;

    private Command commandButton;

    private InputState targetState;

    private OrderProducer orderProducer;

    public StateTransition(InputState currentState,
                           String commandKey, Command commandButton, InputState targetState,
                           OrderProducer orderProducer) {
        this.currentState = currentState;
        this.commandKey = commandKey;
        this.commandButton = commandButton;
        this.targetState = targetState;
        this.orderProducer = orderProducer;
    }

    public boolean match(InputState currentState, String command) {
        return this.currentState == currentState
                && this.commandKey.equals(command);
    }

    public boolean match(InputState currentState) {
        return this.currentState == currentState;
    }

    public InputState getTargetState() {
        return targetState;
    }

    public Order<?> getOrder(GameState gameState) {
        if (orderProducer == null) {
            return null;
        }
        return orderProducer.produce(gameState, gameState.selectionManager.getSelected());
    }

    public Command getCommandButton() {
        return commandButton;
    }

}
