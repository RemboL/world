package pl.rembol.jme3.rts.gameobjects.order;

import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;

import java.util.List;

@FunctionalInterface
public interface OrderProducer {

    Order<?> produce(RtsGameState gameState, List<Selectable> selectableList);

}
