package pl.rembol.jme3.world.ballman;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.world.ballman.action.AttackAction;
import pl.rembol.jme3.world.ballman.action.GatherResourcesAction;
import pl.rembol.jme3.world.ballman.action.MoveTowardsLocationAction;
import pl.rembol.jme3.world.ballman.action.MoveTowardsTargetAction;
import pl.rembol.jme3.world.interfaces.ActionQueueControl;
import pl.rembol.jme3.world.interfaces.Moving;
import pl.rembol.jme3.world.interfaces.WithDefaultAction;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.player.WithOwner;
import pl.rembol.jme3.world.resources.deposits.ResourceDeposit;
import pl.rembol.jme3.world.selection.Destructable;

import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class BallManControl extends ActionQueueControl<BallMan> implements
        WithDefaultAction, Moving {

    private Vector3f targetDirection;
    private float targetVelocity = 0;

    private ApplicationContext applicationContext;

    public BallManControl(ApplicationContext applicationContext, BallMan ballMan) {
        super(ballMan);
        this.applicationContext = applicationContext;
    }

    @Override
    protected void controlUpdate(float tpf) {
        super.controlUpdate(tpf);

        BetterCharacterControl characterControl = getCharacterControl();

        if (targetDirection != null) {
            characterControl.setViewDirection(characterControl
                    .getViewDirection().add(targetDirection).setY(0)
                    .normalize());
        }

        characterControl.setWalkDirection(characterControl.getViewDirection()
                .mult((targetVelocity + characterControl.getWalkDirection()
                        .length()) / 2));
    }

    private BetterCharacterControl getCharacterControl() {
        return unit.getNode().getControl(BetterCharacterControl.class);
    }

    public void lookTowards(Vector3f location) {
        targetDirection = location
                .subtract(unit.getNode().getWorldTranslation()).setY(0)
                .normalize();
    }

    public void setTargetVelocity(float targetVelocity) {
        this.targetVelocity = targetVelocity;
    }

    @Override
    public void performDefaultAction(WithNode target) {
        if (target instanceof ResourceDeposit) {
            setAction(applicationContext.getAutowireCapableBeanFactory()
                    .createBean(GatherResourcesAction.class)
                    .init(unit, ResourceDeposit.class.cast(target)));
        } else if (WithOwner.class.isInstance(target)
                && !WithOwner.class.cast(target).getOwner()
                        .equals(unit.getOwner())
                && Destructable.class.isInstance(target)) {
            setAction(applicationContext.getAutowireCapableBeanFactory()
                    .createBean(AttackAction.class)
                    .init(Destructable.class.cast(target)));
        } else {
            setAction(applicationContext.getAutowireCapableBeanFactory()
                    .createBean(MoveTowardsTargetAction.class).init(target, 5f));
        }
    }

    @Override
    public void performDefaultAction(Vector2f target) {
        setAction(applicationContext.getAutowireCapableBeanFactory()
                .createBean(MoveTowardsLocationAction.class).init(target, 1f));
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
