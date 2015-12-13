package pl.rembol.jme3.world.ballman;

import com.jme3.animation.LoopMode;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.control.ActionQueueControl;
import pl.rembol.jme3.rts.gameobjects.control.DefaultActionControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;
import pl.rembol.jme3.rts.gameobjects.selection.Destructable;
import pl.rembol.jme3.rts.player.WithOwner;
import pl.rembol.jme3.world.ballman.action.AttackAction;
import pl.rembol.jme3.world.ballman.action.EnterHouseAction;
import pl.rembol.jme3.world.ballman.action.GatherResourcesAction;
import pl.rembol.jme3.world.building.house.House;
import pl.rembol.jme3.world.resources.deposits.ResourceDeposit;

public class BallManControl extends ActionQueueControl<BallMan> implements
        DefaultActionControl {

    public BallManControl(GameState gameState, BallMan ballMan) {

        super(gameState, ballMan);
    }

    @Override
    public void performDefaultAction(WithNode target) {
        if (target instanceof ResourceDeposit) {
            setAction(new GatherResourcesAction(gameState, unit, ResourceDeposit.class.cast(target)));
        } else if (WithOwner.class.isInstance(target)
                && !WithOwner.class.cast(target).getOwner()
                .equals(unit.getOwner())
                && Destructable.class.isInstance(target)) {
            setAction(new AttackAction(gameState, Destructable.class.cast(target)));
        } else if (target instanceof House && House.class.cast(target).getOwner().equals(unit.getOwner())) {
            setAction(new EnterHouseAction(gameState, (House) target));
        } else {
            super.performDefaultAction(target);
        }
    }

    @Override
    protected void onEmptyQueue() {
        unit.setAnimation("stand", LoopMode.DontLoop);
    }

    @Override
    protected void onFinishedAction() {
        unit.setAnimation("stand", LoopMode.DontLoop);
    }
}
