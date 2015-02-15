package pl.rembol.jme3.world.ballman.order;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;

public class OrderFactory {

	public static final String ORDER_MOVE = "order_move";
	public static final String ORDER_DEFAULT = "order_default";
	public static final String ORDER_FLATTEN = "order_flatten";
	public static final String ORDER_BUILD_HOUSE = "order_buildHouse";
	public static final String ORDER_SELECT = "order_select";

	public Map<String, Class<? extends Order>> orderMap = new HashMap<>();
	private GameRunningAppState appState;

	public OrderFactory(GameRunningAppState appState) {
		this.appState = appState;
		registerOrder(ORDER_MOVE, MoveOrder.class);
		registerOrder(ORDER_DEFAULT, DefaultActionOrder.class);
		registerOrder(ORDER_FLATTEN, SmoothenTerrainOrder.class);
		registerOrder(ORDER_BUILD_HOUSE, BuildHouseOrder.class);
		registerOrder(ORDER_SELECT, SelectOrder.class);
	}

	public void registerOrder(String command, Class<? extends Order> orderClass) {
		orderMap.put(command, orderClass);
	}

	public Order produceOrder(String command) {
		return produceOrder(
				command,
				appState.getSelectionManager().getSelected().stream()
						.filter(selected -> selected instanceof BallMan)
						.map(selected -> (BallMan) selected)
						.collect(Collectors.toList()));
	}

	public Order produceOrder(String command, List<BallMan> selected) {

		Class<? extends Order> orderClass = orderMap.get(command);

		if (orderClass != null) {
			try {
				Order order = orderClass.getConstructor().newInstance();
				order.setSelected(selected);
				order.setAppState(appState);
				return order;
			} catch (NoSuchMethodException | SecurityException
					| InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		return null;

	}

}
