package pl.rembol.jme3.rts.gameobjects.action;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.pathfinding.Rectangle2f;
import pl.rembol.jme3.rts.gameobjects.interfaces.Solid;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithActionQueueControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithMovingControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;

import java.util.ArrayList;
import java.util.List;

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

    protected boolean assertDistance(WithMovingControl unit, WithNode target,
                                     float distance) {
        if (!isCloseEnough(unit, target, distance)
                && WithMovingControl.class.isInstance(unit)
                && WithActionQueueControl.class.isInstance(unit)) {
            WithActionQueueControl.class.cast(unit)
                    .actionQueueControl()
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
                && WithActionQueueControl.class.isInstance(unit)) {
            WithActionQueueControl.class
                    .cast(unit)
                    .actionQueueControl()
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
            return Solid.class.cast(target).getBoundingRectangle().withBuffer(distance)
                    .isInside(unit.getLocation());
        } else {
            return unit.getNode().getWorldTranslation()
                    .distance(target.getLocation()) < target
                    .getWidth() + distance;
        }
    }

    protected boolean isCloseEnough(WithNode unit, Vector2f target,
                                    float distance) {
        return new Rectangle2f(target)
                .withBuffer(distance + 1)
                .isInside(unit.getLocation());
    }

    public Action<?> withParent(Action<?> action) {
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
