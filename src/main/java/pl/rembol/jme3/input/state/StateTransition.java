package pl.rembol.jme3.input.state;

public class StateTransition {

	private InputState currentState;
	private String command;
	private InputState targetState;
	private String order;

	public StateTransition(InputState currentState, String command,
			InputState targetState, String order) {
		this.currentState = currentState;
		this.command = command;
		this.targetState = targetState;
		this.order = order;
	}

	public boolean match(InputState currentState, String command) {
		return this.currentState == currentState
				&& this.command.equals(command);
	}

	public InputState getTargetState() {
		return targetState;
	}

	public String getOrder() {
		return order;
	}

}
