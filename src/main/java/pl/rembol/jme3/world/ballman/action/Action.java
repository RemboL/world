package pl.rembol.jme3.world.ballman.action;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import pl.rembol.jme3.world.Solid;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.BallMan.Hand;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.pathfinding.Rectangle2f;
import pl.rembol.jme3.world.smallobject.SmallObject;

public abstract class Action implements ApplicationContextAware {

    private boolean isStarted = false;
    protected ApplicationContext applicationContext;

    protected boolean start(BallMan ballMan) {
        return true;
    }

    public void act(BallMan ballMan, float tpf) {
        if (!isStarted) {
            isStarted = start(ballMan);

            if (!isStarted) {
                return;
            }
        }
        doAct(ballMan, tpf);
    }

    abstract protected void doAct(BallMan ballMan, float tpf);

    public boolean isFinished(BallMan ballMan) {
        return false;
    }

    public void finish() {
        stop();
    }

    public void stop() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected boolean assertWielded(BallMan ballMan,
            Class<? extends SmallObject> wieldedClass) {
        if (!wieldedClass.isInstance(ballMan.getWieldedObject(Hand.RIGHT))) {
            try {
                ballMan.addActionOnStart(applicationContext
                        .getAutowireCapableBeanFactory()
                        .createBean(SwitchWeaponAction.class)
                        .init(wieldedClass.newInstance().init(
                                applicationContext)));
            } catch (BeansException | IllegalStateException
                    | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return false;
        }

        return true;
    }

    protected boolean assertDistance(BallMan ballMan, WithNode target,
            float distance) {
        if (!isCloseEnough(ballMan, target, distance)) {
            ballMan.addActionOnStart(applicationContext
                    .getAutowireCapableBeanFactory()
                    .createBean(MoveTowardsTargetAction.class)
                    .init(target, distance));
            return false;
        }

        return true;
    }

    protected boolean isCloseEnough(BallMan ballMan, WithNode target,
            float distance) {
        if (target instanceof Solid) {
            return new Rectangle2f(Solid.class.cast(target), distance)
                    .isInside(ballMan.getNode().getWorldTranslation());
        } else {
            return ballMan.getLocation().distance(
                    target.getNode().getWorldTranslation()) < target.getWidth()
                    + distance;
        }
    }
}
