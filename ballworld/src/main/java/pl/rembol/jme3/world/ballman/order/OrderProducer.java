package pl.rembol.jme3.world.ballman.order;

import java.util.List;

import pl.rembol.jme3.rts.unit.order.Order;
import pl.rembol.jme3.rts.unit.selection.Selectable;
import pl.rembol.jme3.world.GameState;

@FunctionalInterface
public interface OrderProducer {

    Order<?> produce(GameState gameState, List<Selectable> selectableList);
    
}
