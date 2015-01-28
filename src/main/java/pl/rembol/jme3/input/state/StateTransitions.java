package pl.rembol.jme3.input.state;

import static pl.rembol.jme3.input.state.InputState.DEFAULT;
import static pl.rembol.jme3.input.state.InputState.ISSUE_MOVE;
import static pl.rembol.jme3.input.state.InputState.ISSUE_FLATTEN;
import static pl.rembol.jme3.input.state.InputState.ISSUE_BUILD_HOUSE;
import static pl.rembol.jme3.input.state.InputStateManager.LEFT_CLICK;
import static pl.rembol.jme3.input.state.InputStateManager.M;
import static pl.rembol.jme3.input.state.InputStateManager.F;
import static pl.rembol.jme3.input.state.InputStateManager.B;
import static pl.rembol.jme3.input.state.InputStateManager.RIGHT_CLICK;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static pl.rembol.jme3.world.ballman.order.OrderFactory.*;

public class StateTransitions {

	private static final List<StateTransition> transitions = new ArrayList<>();

	static {
		register(DEFAULT, LEFT_CLICK, DEFAULT, ORDER_SELECT);
		register(DEFAULT, RIGHT_CLICK, DEFAULT, ORDER_DEFAULT);
		register(DEFAULT, M, ISSUE_MOVE, null);
		register(DEFAULT, F, ISSUE_FLATTEN, null);
		register(DEFAULT, B, ISSUE_BUILD_HOUSE, null);

		register(ISSUE_MOVE, LEFT_CLICK, DEFAULT, ORDER_MOVE);
		register(ISSUE_MOVE, RIGHT_CLICK, DEFAULT, null);

		register(ISSUE_FLATTEN, LEFT_CLICK, DEFAULT, ORDER_FLATTEN);
		register(ISSUE_FLATTEN, RIGHT_CLICK, DEFAULT, null);

		register(ISSUE_BUILD_HOUSE, LEFT_CLICK, DEFAULT, ORDER_BUILD_HOUSE);
		register(ISSUE_BUILD_HOUSE, RIGHT_CLICK, DEFAULT, null);
	}

	private static void register(InputState startingState, String command,
			InputState finishState, String order) {
		transitions.add(new StateTransition(startingState, command,
				finishState, order));
	}

	public Optional<StateTransition> match(InputState state, String command) {
		return transitions.stream()
				.filter(transition -> transition.match(state, command))
				.findFirst();
	}

}
