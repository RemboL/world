package pl.rembol.jme3.world.input.state;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.rembol.jme3.world.ballman.order.Order;
import pl.rembol.jme3.world.ballman.order.OrderFactory;
import pl.rembol.jme3.world.hud.ActionBox;
import pl.rembol.jme3.world.input.state.SelectionManager.SelectionType;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

@Component
public class InputStateManager {

	public static final String M = "command_m";

	public static final String F = "command_f";

	public static final String B = "command_b";

	public static final String H = "command_h";

	public static final String W = "command_w";

	public static final String C = "command_c";

	public static final String R = "command_r";

	public static final String RIGHT_CLICK = "command_rightClick";

	public static final String LEFT_CLICK = "command_leftClick";

	public static final String MOUSE_MOVE = "command_mouseMove";

	private InputState currentState = InputState.DEFAULT;

	private Order<?> currentOrder = null;

	private StateTransitions transitions = new StateTransitions();

	@Autowired
	private ActionBox actionBox;

	@Autowired
	private OrderFactory orderFactory;

	@Autowired
	private SelectionManager selectionManager;

	@Autowired
	private BuildingSilhouetteManager buildingSilhouetteManager;

	public InputState getCurrentState() {
		return currentState;
	}

	public void click(String command, WithNode target) {
		if (currentState == InputState.ISSUE_ORDER) {
			switch (command) {
			case InputStateManager.LEFT_CLICK:
				performOrder(target);
				break;
			case InputStateManager.RIGHT_CLICK:
				cancelOrder();
				break;
			}
		} else if (currentState == InputState.ISSUE_BUILD_ORDER) {
			switch (command) {
			case InputStateManager.LEFT_CLICK:
				Vector3f location = buildingSilhouetteManager
						.getSilhouettePosition();
				if (location != null) {
					performOrder(new Vector2f(location.x, location.z));
				} else {
					cancelOrder();
				}
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
				if (selectionManager.getSelectionType() == SelectionType.UNIT) {
					orderFactory.produceOrder(OrderFactory.ORDER_DEFAULT)
							.perform(target);
				}
				break;
			}
		}

		actionBox.updateActionButtons();
		buildingSilhouetteManager.removeSilhouette();
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
		} else if (currentState == InputState.ISSUE_BUILD_ORDER) {
			switch (command) {
			case InputStateManager.LEFT_CLICK:
				Vector3f location = buildingSilhouetteManager
						.getSilhouettePosition();
				if (location != null) {
					performOrder(new Vector2f(location.x, location.z));
				} else {
					cancelOrder();
				}
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
				if (selectionManager.getSelectionType() == SelectionType.UNIT) {
					orderFactory.produceOrder(OrderFactory.ORDER_DEFAULT)
							.perform(target);
				}
				break;
			}
		}

		actionBox.updateActionButtons();
		buildingSilhouetteManager.removeSilhouette();
	}

	public void type(String command) {
		System.out.println("typing " + command);
		getTransitionAndChangeState(command);

		actionBox.updateActionButtons();
	}

	public void cancelOrder() {
		currentOrder = null;
		currentState = InputState.DEFAULT;

		actionBox.updateActionButtons();
		buildingSilhouetteManager.removeSilhouette();
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

	private Optional<StateTransition> getTransitionAndChangeState(String command) {
		Optional<StateTransition> transition = transitions.match(currentState,
				selectionManager.getSelectionType(), command);

		if (transition.isPresent()) {
			currentState = transition.get().getTargetState();
			if (currentState == InputState.ISSUE_ORDER
					|| currentState == InputState.ISSUE_BUILD_ORDER) {
				currentOrder = orderFactory.produceOrder(transition.get()
						.getOrder());
				buildingSilhouetteManager.createSilhouette(currentOrder);
			} else if (currentState == InputState.ISSUE_ORDER_IMMEDIATELY) {
				currentOrder = null;
				orderFactory.produceOrder(transition.get().getOrder()).perform(
						Selectable.class.cast(null));
				currentState = InputState.DEFAULT;
			} else {
				currentOrder = null;
			}
		}

		return transition;
	}

	public List<Command> getAvailableCommands() {
		selectionManager.getSelectionType();
		return transitions
				.match(currentState, selectionManager.getSelectionType())
				.stream().map(transition -> transition.getCommandButton())
				.collect(Collectors.toList());
	}

}
