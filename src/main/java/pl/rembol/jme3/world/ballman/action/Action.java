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

public abstract class Action<T extends WithNode> implements
        ApplicationContextAware {

    private boolean isStarted = false;
    private boolean isCancelled = false;
    protected ApplicationContext applicationContext;

    protected Action<?> parent = null;
    protected List<Action<?>> children = new ArrayList<>();

    protected boolean start(T unit) {
        return true;
    }

    public void act(T unit, float tpf) {
        if (!isStarted) {
            isStarted = start(unit);

            if (!isStarted) {
                return;
            }
        }
        doAct(unit, tpf);
    }

    abstract protected void doAct(T unit, float tpf);

    public boolean isFinished(T unit) {
        return false;
    }

    protected Action<?> superParent() {
        if (parent == null) {
            return this;
        } else {
            return parent.superParent();
        }
    }

    protected void doCancel() {
        isCancelled = true;
        for (Action<?> child : children) {
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
            Optional<Class<? extends Tool>> wieldedClass) {
        if (!wieldedClass.isPresent()) {
            if (ballMan.getWieldedObject(Hand.RIGHT) != null) {
                ballMan.control().addActionOnStart(
                        applicationContext.getAutowireCapableBeanFactory()
                                .createBean(SwitchToolAction.class)
                                .init(Optional.empty()).withParent(this));
                return false;
            }
            return true;
        }

        if (!wieldedClass.get()
                .isInstance(ballMan.getWieldedObject(Hand.RIGHT))) {
            try {
                Optional<Tool> toolFromInventory = ballMan.inventory().get(
                        wieldedClass.get());
                if (toolFromInventory.isPresent()) {
                    ballMan.control().addActionOnStart(
                            applicationContext.getAutowireCapableBeanFactory()
                                    .createBean(SwitchToolAction.class)
                                    .init(toolFromInventory).withParent(this));
                    return false;
                } else {
                    ballMan.control()
                            .addActionOnStart(
                                    applicationContext
                                            .getAutowireCapableBeanFactory()
                                            .createBean(
                                                    GetToolFromToolshopAction.class)
                                            .init(wieldedClass.get())
                                            .withParent(this));
                    return false;
                }
            } catch (BeansException | IllegalStateException e) {
                e.printStackTrace();
            }

            return false;
        }

        return true;
    }

    protected boolean assertDistance(WithNode unit, WithNode target,
            float distance) {
        if (!isCloseEnough(unit, target, distance)
                && BallMan.class.isInstance(unit)) {
            BallMan.class
                    .cast(unit)
                    .control()
                    .addActionOnStart(
                            applicationContext.getAutowireCapableBeanFactory()
                                    .createBean(MoveTowardsTargetAction.class)
                                    .init(target, distance).withParent(this));
            return false;
        }

        return true;
    }

    protected boolean isCloseEnough(WithNode unit, WithNode target,
            float distance) {
        if (target instanceof Solid) {
            return new Rectangle2f(Solid.class.cast(target), distance)
                    .isInside(unit.getNode().getWorldTranslation());
        } else {
            return unit.getNode().getWorldTranslation()
                    .distance(target.getNode().getWorldTranslation()) < target
                    .getWidth() + distance;
        }
    }

    protected Action<?> withParent(Action<?> action) {
        parent = action;
        action.children.add(this);
        return this;
    }

}
