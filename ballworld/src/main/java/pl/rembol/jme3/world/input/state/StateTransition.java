package pl.rembol.jme3.world.input.state;

import pl.rembol.jme3.rts.unit.order.Order;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.order.OrderProducer;
import pl.rembol.jme3.world.input.state.SelectionManager.SelectionType;

public class StateTransition {

    private InputState currentState;

    private String commandKey;

    private Command commandButton;

    private InputState targetState;

    private OrderProducer orderProducer;

    private SelectionType type;

    public StateTransition(InputState currentState, SelectionType type,
                           String commandKey, Command commandButton, InputState targetState,
                           OrderProducer orderProducer) {
        this.currentState = currentState;
        this.type = type;
        this.commandKey = commandKey;
        this.commandButton = commandButton;
        this.targetState = targetState;
        this.orderProducer = orderProducer;
    }

    public boolean match(InputState currentState, SelectionType type,
                         String command) {
        return this.currentState == currentState && this.type == type
                && this.commandKey.equals(command);
    }

    public boolean match(InputState currentState, SelectionType type) {
        return this.currentState == currentState && this.type == type;
    }

    public InputState getTargetState() {
        return targetState;
    }

    public Order<?> getOrder(GameState gameState) {
        if (orderProducer == null) {
            return null;
        }
        Order order = orderProducer.produce(gameState, gameState.selectionManager.getSelected());
//        order.setSelected(gameState.selectionManager.getSelected().stream().filter(order::isApplicableFor).collect(
//                Collectors.toList()));
        return order;
    }

    public Command getCommandButton() {
        return commandButton;
    }

}
