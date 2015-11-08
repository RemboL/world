package pl.rembol.jme3.world.ballman.order;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.unit.interfaces.WithMovingControl;
import pl.rembol.jme3.rts.unit.order.Order;
import pl.rembol.jme3.rts.unit.selection.Selectable;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.rts.unit.action.MoveTowardsLocationAction;
import pl.rembol.jme3.rts.unit.action.MoveTowardsTargetAction;
import pl.rembol.jme3.rts.unit.interfaces.WithNode;

public class MoveOrder extends Order<BallMan> {

    public MoveOrder(GameState gameState) {
        super(gameState);
    }

    @Override
    protected void doPerform(BallMan ballMan, Vector2f location) {
        ballMan.control().setAction(
                new MoveTowardsLocationAction(gameState, ballMan, location, 5f));
    }

    @Override
    protected void doPerform(BallMan ballMan, WithNode target) {
        ballMan.control().setAction(
                new MoveTowardsTargetAction(gameState, ballMan, target, 5f));
    }

    @Override
    public boolean isApplicableFor(Selectable unit) {
        return unit instanceof WithMovingControl;
    }

}
