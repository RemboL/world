package pl.rembol.jme3.rts.input.state;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.order.Order;
import pl.rembol.jme3.rts.gameobjects.order.OrderProducer;

public class StateTransition {

    private InputState currentState;

    private Command commandButton;

    private InputState targetState;

    private OrderProducer orderProducer;

    public StateTransition(InputState currentState, Command commandButton, InputState targetState,
                           OrderProducer orderProducer) {
        this.currentState = currentState;
        this.commandButton = commandButton;
        this.targetState = targetState;
        this.orderProducer = orderProducer;
    }

    public boolean match(InputState currentState, int key) {
        return this.currentState == currentState
                && this.commandButton.getKey() == key;
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
