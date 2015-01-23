package pl.rembol.jme3.world.ballman.order;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.rembol.jme3.world.ballman.BallMan;

public class OrderFactory {

	public Map<String, Class<? extends Order>> orderMap = new HashMap<>();

	public OrderFactory() {
		registerOrder("move", MoveOrder.class);
		registerOrder("flatten", SmoothenTerrainOrder.class);
		registerOrder("buildHouse", BuildHouseOrder.class);
	}

	public void registerOrder(String command, Class<? extends Order> orderClass) {
		orderMap.put(command, orderClass);
	}

	public Order produceOrder(String command, List<BallMan> selected) {

		Class<? extends Order> orderClass = orderMap.get(command);

		if (orderClass != null) {
			try {
				Order order = orderClass.getConstructor().newInstance();
				order.setSelected(selected);
				System.out.println("producing order " + order);
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
