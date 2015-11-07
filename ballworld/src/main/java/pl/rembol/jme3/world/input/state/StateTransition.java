package pl.rembol.jme3.world.input.state;

import pl.rembol.jme3.world.input.state.SelectionManager.SelectionType;

public class StateTransition {

    private InputState currentState;
    private String commandKey;
    private Command commandButton;
    private InputState targetState;
    private String order;
    private SelectionType type;

    public StateTransition(InputState currentState, SelectionType type,
                           String commandKey, Command commandButton, InputState targetState, String order) {
        this.currentState = currentState;
        this.type = type;
        this.commandKey = commandKey;
        this.commandButton = commandButton;
        this.targetState = targetState;
        this.order = order;
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

    public String getOrder() {
        return order;
    }

    public Command getCommandButton() {
        return commandButton;
    }

}
