package pl.rembol.jme3.input.state;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pl.rembol.jme3.input.state.SelectionManager.SelectionType;
import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.ballman.order.Order;
import pl.rembol.jme3.world.ballman.order.OrderFactory;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.Vector2f;

public class InputStateManager {

	public static final String M = "command_m";

	public static final String F = "command_f";

	public static final String B = "command_b";

	public static final String C = "command_c";

	public static final String RIGHT_CLICK = "command_rightClick";

	public static final String LEFT_CLICK = "command_leftClick";

	private InputState currentState = InputState.DEFAULT;

	private Order currentOrder = null;

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

	public void click(String command, Selectable target) {
		if (currentState == InputState.ISSUE_ORDER) {
			switch (command) {
			case InputStateManager.LEFT_CLICK:
				performOrder(target);
				break;
			case InputStateManager.RIGHT_CLICK:
				cancelOrder();
				break;
			}
		} else if (currentState == InputState.DEFAULT) {
			switch (command) {
			case InputStateManager.LEFT_CLICK:
				orderFactory.produceOrder(OrderFactory.ORDER_SELECT).perform(
						target);
				break;
			case InputStateManager.RIGHT_CLICK:
				if (appState.getSelectionManager().getSelectionType() == SelectionType.UNIT) {
					orderFactory.produceOrder(OrderFactory.ORDER_DEFAULT)
							.perform(target);
				}
				break;
			}
		}

		appState.getHudManager().updateActionButtons();
	}

	public void click(String command, Vector2f target) {
		if (currentState == InputState.ISSUE_ORDER) {
			switch (command) {
			case InputStateManager.LEFT_CLICK:
				performOrder(target);
				break;
			case InputStateManager.RIGHT_CLICK:
				cancelOrder();
				break;
			}
		} else if (currentState == InputState.DEFAULT) {
			switch (command) {
			case InputStateManager.LEFT_CLICK:
				orderFactory.produceOrder(OrderFactory.ORDER_SELECT).perform(
						target);
				break;
			case InputStateManager.RIGHT_CLICK:
				if (appState.getSelectionManager().getSelectionType() == SelectionType.UNIT) {
					orderFactory.produceOrder(OrderFactory.ORDER_DEFAULT)
							.perform(target);
				}
				break;
			}
		}

		appState.getHudManager().updateActionButtons();
	}

	public void type(String command) {
		getTransitionAndChangeState(command);

		appState.getHudManager().updateActionButtons();
	}

	private void cancelOrder() {
		currentOrder = null;
		currentState = InputState.DEFAULT;
	}

	private void performOrder(Selectable target) {
		currentOrder.perform(target);
		currentOrder = null;
		currentState = InputState.DEFAULT;

	}

	private void performOrder(Vector2f target) {
		currentOrder.perform(target);
		currentOrder = null;
		currentState = InputState.DEFAULT;

	}

	private Optional<StateTransition> getTransitionAndChangeState(String command) {
		Optional<StateTransition> transition = transitions.match(currentState,
				appState.getSelectionManager().getSelectionType(), command);

		if (transition.isPresent()) {
			currentState = transition.get().getTargetState();
			if (currentState == InputState.ISSUE_ORDER) {
				currentOrder = orderFactory.produceOrder(transition.get()
						.getOrder());
			} else {
				currentOrder = null;
			}
		}
		return transition;
	}

	public List<Command> getAvailableCommands() {
		appState.getSelectionManager().getSelectionType();
		return transitions
				.match(currentState,
						appState.getSelectionManager().getSelectionType())
				.stream().map(transition -> transition.getCommandButton())
				.collect(Collectors.toList());
	}

}
