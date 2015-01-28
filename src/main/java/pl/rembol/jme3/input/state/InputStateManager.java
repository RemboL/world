package pl.rembol.jme3.input.state;

import java.util.Optional;

import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.ballman.order.OrderFactory;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.Vector2f;

public class InputStateManager {

	public static final String M = "command_m";

	public static final String F = "command_f";

	public static final String B = "command_b";

	public static final String RIGHT_CLICK = "command_rightClick";

	public static final String LEFT_CLICK = "command_leftClick";

	private InputState currentState = InputState.DEFAULT;

	private GameRunningAppState appState;

	private StateTransitions transitions = new StateTransitions();

	private OrderFactory orderFactory;

	public InputStateManager(GameRunningAppState appState,
			OrderFactory orderFactory) {
		this.appState = appState;
		this.orderFactory = orderFactory;
	}

	public InputState getCurrentState() {
		return currentState;
	}

	public void issueCommand(String command, Selectable target) {
		Optional<StateTransition> transition = getTransitionAndChangeState(command);

		if (transition.isPresent()) {
			orderFactory.produceOrder(transition.get().getOrder()).perform(
					target);
		}
	}

	public void issueCommand(String command, Vector2f target) {
		Optional<StateTransition> transition = getTransitionAndChangeState(command);

		if (transition.isPresent()) {
			orderFactory.produceOrder(transition.get().getOrder()).perform(
					target);
		}
	}

	public void issueCommand(String command) {
		getTransitionAndChangeState(command);
	}

	private Optional<StateTransition> getTransitionAndChangeState(String command) {
		Optional<StateTransition> transition = transitions.match(currentState,
				command);

		if (transition.isPresent()) {
			currentState = transition.get().getTargetState();
		}
		return transition;
	}

}
