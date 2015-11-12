package pl.rembol.jme3.rts.gameobjects.order;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;

import java.util.List;

@FunctionalInterface
public interface OrderProducer {

    Order<?> produce(GameState gameState, List<Selectable> selectableList);

}
