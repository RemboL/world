package pl.rembol.jme3.world.ballman;

import com.jme3.animation.LoopMode;
import com.jme3.math.Vector2f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.action.AttackAction;
import pl.rembol.jme3.world.ballman.action.EnterHouseAction;
import pl.rembol.jme3.world.ballman.action.GatherResourcesAction;
import pl.rembol.jme3.world.ballman.action.MoveTowardsLocationAction;
import pl.rembol.jme3.world.ballman.action.MoveTowardsTargetAction;
import pl.rembol.jme3.world.building.house.House;
import pl.rembol.jme3.world.controls.ActionQueueControl;
import pl.rembol.jme3.world.interfaces.WithDefaultAction;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.player.WithOwner;
import pl.rembol.jme3.world.resources.deposits.ResourceDeposit;
import pl.rembol.jme3.world.selection.Destructable;

public class BallManControl extends ActionQueueControl<BallMan> implements
        WithDefaultAction {

    private GameState gameState;

    public BallManControl(GameState gameState, BallMan ballMan) {
        super(ballMan);
        this.gameState = gameState;
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
        }else {
            setAction(new MoveTowardsTargetAction(gameState, unit, target, 5f));
        }
    }

    @Override
    public void performDefaultAction(Vector2f target) {
        setAction(new MoveTowardsLocationAction(gameState, unit, target, 1f));
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
