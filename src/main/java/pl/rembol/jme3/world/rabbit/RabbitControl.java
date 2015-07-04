package pl.rembol.jme3.world.rabbit;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.world.ballman.action.MoveTowardsLocationAction;
import pl.rembol.jme3.world.ballman.action.WaitAction;
import pl.rembol.jme3.world.interfaces.ActionQueueControl;
import pl.rembol.jme3.world.interfaces.Moving;

import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class RabbitControl extends ActionQueueControl<Rabbit> implements Moving {

    private Vector3f targetDirection;
    private float targetVelocity = 0;

    private ApplicationContext applicationContext;

    protected RabbitControl(ApplicationContext applicationContext, Rabbit unit) {
        super(unit);
        this.applicationContext = applicationContext;
    }

    @Override
    protected void onEmptyQueue() {
        unit.setAnimation("stand", LoopMode.DontLoop);

        addAction(applicationContext
                .getAutowireCapableBeanFactory()
                .createBean(MoveTowardsLocationAction.class)
                .init(new Vector2f(unit.getLocation().x
                        + FastMath.nextRandomInt(-10, 10), unit.getLocation().z
                        + FastMath.nextRandomInt(-10, 10)), 2));
        addAction(applicationContext.getAutowireCapableBeanFactory()
                .createBean(WaitAction.class)
                .init(FastMath.nextRandomInt(5, 10)));
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

    @Override
    protected void onFinishedAction() {
        unit.setAnimation("stand", LoopMode.DontLoop);
    }

}
