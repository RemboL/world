package pl.rembol.jme3.world.ballman.order;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.unit.order.Order;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.rts.unit.interfaces.WithNode;
import pl.rembol.jme3.rts.unit.selection.Selectable;

public class SelectOrder extends Order<Selectable> {

    public SelectOrder(GameState gameState) {
        super(gameState);
    }

    @Override
    public void perform(WithNode target) {
        if (Selectable.class.isInstance(target)) {
            // TODO FIXME
            GameState.class.cast(gameState).selectionManager.select(Selectable.class.cast(target));
        }
    }

    @Override
    protected void doPerform(Selectable ballMan, Vector2f location) {
        // doNothing
    }

    @Override
    protected void doPerform(Selectable ballMan, WithNode target) {
        // doNothing
    }

}
