package pl.rembol.jme3.world.ballman.order;

import java.util.List;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.unit.action.MoveTowardsLocationAction;
import pl.rembol.jme3.rts.unit.action.MoveTowardsTargetAction;
import pl.rembol.jme3.rts.unit.interfaces.WithMovingControl;
import pl.rembol.jme3.rts.unit.interfaces.WithNode;
import pl.rembol.jme3.rts.unit.order.Order;
import pl.rembol.jme3.rts.unit.selection.Selectable;
import pl.rembol.jme3.world.unit.Unit;

public class MoveOrder extends Order<Unit> {

    public MoveOrder(GameState gameState, List<Selectable> selectableList) {
        super(gameState, selectableList);
    }

    @Override
    protected void doPerform(Unit unit, Vector2f location) {
        unit.actionQueueControl().setAction(
                new MoveTowardsLocationAction(gameState, unit, location, 5f));
    }

    @Override
    protected void doPerform(Unit unit, WithNode target) {
        unit.actionQueueControl().setAction(
                new MoveTowardsTargetAction(gameState, unit, target, 5f));
    }

    @Override
    public boolean isApplicableFor(Selectable unit) {
        return unit instanceof WithMovingControl;
    }

}
