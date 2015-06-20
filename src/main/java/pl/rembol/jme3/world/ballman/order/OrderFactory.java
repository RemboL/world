package pl.rembol.jme3.world.ballman.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import pl.rembol.jme3.world.input.state.SelectionManager;
import pl.rembol.jme3.world.selection.Selectable;

@Component
public class OrderFactory implements ApplicationContextAware {

	public static final String ORDER_MOVE = "order_move";
	public static final String ORDER_DEFAULT = "order_default";
	public static final String ORDER_FLATTEN = "order_flatten";
	public static final String ORDER_BUILD_HOUSE = "order_buildHouse";
	public static final String ORDER_BUILD_TOOLSHOP = "order_buildToolshop";
    public static final String ORDER_BUILD_WAREHOUSE = "order_buildWarehouse";
	public static final String ORDER_SELECT = "order_select";
	public static final String ORDER_RECRUIT = "order_recruit";

	public Map<String, Class<? extends Order<?>>> orderMap = new HashMap<>();

	@Autowired
	private SelectionManager selectionManager;
	private ApplicationContext applicationContext;

	public OrderFactory() {
		registerOrder(ORDER_MOVE, MoveOrder.class);
		registerOrder(ORDER_DEFAULT, DefaultActionOrder.class);
		registerOrder(ORDER_FLATTEN, SmoothenTerrainOrder.class);
		registerOrder(ORDER_BUILD_HOUSE, BuildHouseOrder.class);
		registerOrder(ORDER_BUILD_TOOLSHOP, BuildToolshopOrder.class);
		registerOrder(ORDER_BUILD_WAREHOUSE, BuildWarehouseOrder.class);
		registerOrder(ORDER_SELECT, SelectOrder.class);
		registerOrder(ORDER_RECRUIT, RecruitOrder.class);
	}

	public void registerOrder(String command,
			Class<? extends Order<?>> orderClass) {
		orderMap.put(command, orderClass);
	}

	public Order<?> produceOrder(String command) {
		return produceOrder(command, selectionManager.getSelected());
	}

	public Order<?> produceOrder(String command, List<Selectable> selected) {
		Class<? extends Order<?>> orderClass = orderMap.get(command);

		if (orderClass != null) {
			try {
				Order<Selectable> order = (Order<Selectable>) applicationContext
						.getAutowireCapableBeanFactory().createBean(orderClass);
				order.setSelected(selected);
				return order;
			} catch (SecurityException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		return null;

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
