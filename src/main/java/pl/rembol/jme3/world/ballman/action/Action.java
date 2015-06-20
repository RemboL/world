package pl.rembol.jme3.world.ballman.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import pl.rembol.jme3.world.Solid;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.BallMan.Hand;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.pathfinding.Rectangle2f;
import pl.rembol.jme3.world.smallobject.tools.Tool;

public abstract class Action implements ApplicationContextAware {

    private boolean isStarted = false;
    private boolean isCancelled = false;
    protected ApplicationContext applicationContext;

    protected Action parent = null;
    protected List<Action> children = new ArrayList<>();

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

    protected Action superParent() {
        if (parent == null) {
            return this;
        } else {
            return parent.superParent();
        }
    }

    protected void doCancel() {
        isCancelled = true;
        for (Action child : children) {
            child.doCancel();
        }
    }

    public void cancel() {
        superParent().doCancel();
    }

    public boolean isCancelled() {
        return isCancelled;
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
            Class<? extends Tool> wieldedClass) {
        if (!wieldedClass.isInstance(ballMan.getWieldedObject(Hand.RIGHT))) {
            try {
                Optional<Tool> toolFromInventory = ballMan.inventory().get(
                        wieldedClass);
                System.out.println("toool " + toolFromInventory);
                if (toolFromInventory.isPresent()) {
                    ballMan.addActionOnStart(applicationContext
                            .getAutowireCapableBeanFactory()
                            .createBean(SwitchToolAction.class)
                            .init(toolFromInventory.get()).withParent(this));
                    return false;
                } else {
                    ballMan.addActionOnStart(applicationContext
                            .getAutowireCapableBeanFactory()
                            .createBean(GetToolFromToolshopAction.class)
                            .init(wieldedClass).withParent(this));
                    return false;
                }
            } catch (BeansException | IllegalStateException e) {
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
                    .init(target, distance).withParent(this));
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

    protected Action withParent(Action action) {
        parent = action;
        action.children.add(this);
        return this;
    }

}
