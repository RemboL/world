package pl.rembol.jme3.world.ballman.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.Solid;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.BallMan.Hand;
import pl.rembol.jme3.world.interfaces.WithMovingControl;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.pathfinding.Rectangle2f;
import pl.rembol.jme3.world.smallobject.tools.Tool;

public abstract class Action<T extends WithNode> {

    protected boolean isStarted = false;

    private boolean isCancelled = false;

    protected GameState gameState;

    protected Action<?> parent = null;

    protected List<Action<?>> children = new ArrayList<>();

    public Action(GameState gameState) {
        this.gameState = gameState;
    }

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
        children.forEach(Action::doCancel);
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

    protected boolean assertWielded(BallMan ballMan,
                                    Optional<Class<? extends Tool>> wieldedClass) {
        if (!wieldedClass.isPresent()) {
            if (ballMan.getWieldedObject(Hand.RIGHT) != null) {
                ballMan.control().addActionOnStart(
                        new SwitchToolAction(gameState, Optional.empty()).withParent(this));
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
                            new SwitchToolAction(gameState, toolFromInventory).withParent(
                                    this));
                    return false;
                } else {
                    ballMan.control().addActionOnStart(
                            new GetToolFromToolshopAction(gameState, wieldedClass.get())
                                    .withParent(this));
                    return false;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            return false;
        }

        return true;
    }

    protected boolean assertDistance(WithMovingControl unit, WithNode target,
                                     float distance) {
        if (!isCloseEnough(unit, target, distance)
                && BallMan.class.isInstance(unit)) {
            BallMan.class
                    .cast(unit)
                    .control()
                    .addActionOnStart(
                            new MoveTowardsTargetAction(gameState, unit, target, distance)
                                    .withParent(this));
            return false;
        }

        return true;
    }

    protected boolean assertDistance(WithMovingControl unit, Vector2f target,
                                     float distance) {
        if (!isCloseEnough(unit, target, distance)
                && BallMan.class.isInstance(unit)) {
            BallMan.class
                    .cast(unit)
                    .control()
                    .addActionOnStart(
                            new MoveTowardsLocationAction(gameState, unit, target, distance)
                                    .withParent(this));
            return false;
        }

        return true;
    }

    protected boolean isCloseEnough(WithNode unit, WithNode target,
                                    float distance) {
        if (target instanceof Solid) {
            return new Rectangle2f(Solid.class.cast(target), distance)
                    .isInside(unit.getLocation());
        } else {
            return unit.getNode().getWorldTranslation()
                    .distance(target.getLocation()) < target
                    .getWidth() + distance;
        }
    }

    protected boolean isCloseEnough(WithNode unit, Vector2f target,
                                    float distance) {
        return new Rectangle2f(target, distance + 1)
                .isInside(unit.getLocation());
    }

    protected Action<?> withParent(Action<?> action) {
        parent = action;
        action.children.add(this);
        return this;
    }

    public boolean isAssignableFrom(Class<? extends Action<?>> actionClass) {
        if (this.getClass().isAssignableFrom(actionClass)) {
            return true;
        }

        if (parent == null) {
            return false;
        }

        return parent.isAssignableFrom(actionClass);
    }

}
