package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.rts.unit.order.Order;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.rts.unit.selection.Selectable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderFactory {

    public static final String ORDER_MOVE = "order_move";
    public static final String ORDER_DEFAULT = "order_default";
    public static final String ORDER_FLATTEN = "order_flatten";
    public static final String ORDER_BUILD_HOUSE = "order_buildHouse";
    public static final String ORDER_BUILD_TOOLSHOP = "order_buildToolshop";
    public static final String ORDER_BUILD_WAREHOUSE = "order_buildWarehouse";
    public static final String ORDER_SELECT = "order_select";
    public static final String ORDER_RECRUIT = "order_recruit";

    public Map<String, Class<? extends Order<? extends Selectable>>> orderMap = new HashMap<>();
    private GameState gameState;

    public OrderFactory(GameState gameState) {
        this.gameState = gameState;
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
                              Class<? extends Order<? extends Selectable>> orderClass) {
        orderMap.put(command, orderClass);
    }

    public Order<?> produceOrder(String command) {
        return produceOrder(command, gameState.selectionManager.getSelected());
    }

    public Order<?> produceOrder(String command, List<Selectable> selected) {
        Class<? extends Order<?>> orderClass = orderMap.get(command);

        if (orderClass != null) {
            try {
                Order<Selectable> order = (Order<Selectable>) orderClass.getConstructor(GameState.class).newInstance(gameState);
                order.setSelected(selected);
                return order;
            } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

}
