package pl.rembol.jme3.input.state;

import static pl.rembol.jme3.input.state.InputState.DEFAULT;
import static pl.rembol.jme3.input.state.InputState.ISSUE_ORDER;
import static pl.rembol.jme3.input.state.InputState.ISSUE_ORDER_IMMEDIATELY;
import static pl.rembol.jme3.input.state.SelectionManager.SelectionType.UNIT;
import static pl.rembol.jme3.input.state.SelectionManager.SelectionType.HOUSE;
import static pl.rembol.jme3.world.ballman.order.OrderFactory.ORDER_BUILD_HOUSE;
import static pl.rembol.jme3.world.ballman.order.OrderFactory.ORDER_FLATTEN;
import static pl.rembol.jme3.world.ballman.order.OrderFactory.ORDER_MOVE;
import static pl.rembol.jme3.world.ballman.order.OrderFactory.ORDER_RECRUIT;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pl.rembol.jme3.input.state.SelectionManager.SelectionType;

public class StateTransitions {

	private static final List<StateTransition> transitions = new ArrayList<>();

	static {
		register(DEFAULT, UNIT, Command.MOVE, ISSUE_ORDER, ORDER_MOVE);
		register(DEFAULT, UNIT, Command.FLATTEN, ISSUE_ORDER, ORDER_FLATTEN);
		register(DEFAULT, UNIT, Command.BUILD, ISSUE_ORDER, ORDER_BUILD_HOUSE);

		register(DEFAULT, HOUSE, Command.RECRUIT, ISSUE_ORDER_IMMEDIATELY,
				ORDER_RECRUIT);

		register(ISSUE_ORDER, UNIT, Command.CANCEL, DEFAULT, null);

	}

	private static void register(InputState startingState, SelectionType type,
			Command commandButton, InputState finishState, String order) {
		transitions.add(new StateTransition(startingState, type, commandButton
				.getCommandKey(), commandButton, finishState, order));
	}

	public Optional<StateTransition> match(InputState state,
			SelectionType type, String command) {
		return transitions.stream()
				.filter(transition -> transition.match(state, type, command))
				.findFirst();
	}

	public List<StateTransition> match(InputState state, SelectionType type) {
		return transitions.stream()
				.filter(transition -> transition.match(state, type))
				.collect(Collectors.toList());
	}

}
