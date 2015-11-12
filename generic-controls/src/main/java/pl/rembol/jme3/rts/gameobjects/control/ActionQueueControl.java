package pl.rembol.jme3.rts.gameobjects.control;

import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.action.Action;
import pl.rembol.jme3.rts.gameobjects.action.MoveTowardsLocationAction;
import pl.rembol.jme3.rts.gameobjects.action.MoveTowardsTargetAction;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithMovingControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;

import java.util.ArrayList;
import java.util.List;

public class ActionQueueControl<T extends WithNode> extends
        AbstractControl implements DefaultActionControl {

    protected GameState gameState;
    protected T unit;

    private List<Action<?>> actionQueue = new ArrayList<>();

    protected ActionQueueControl(GameState gameState, T unit) {
        this.gameState = gameState;
        this.unit = unit;
    }

    @Override
    protected void controlRender(RenderManager paramRenderManager,
                                 ViewPort paramViewPort) {
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (!actionQueue.isEmpty()) {
            Action action = actionQueue.get(0);
            action.act(unit, tpf);

            if (action.isCancelled() || action.isFinished(unit)) {
                action.finish();
                actionQueue.remove(action);
                onFinishedAction();
            }
        } else {
            onEmptyQueue();
        }
    }

    protected void onFinishedAction() {
    }

    protected void onEmptyQueue() {
    }

    public void setAction(Action<?> action) {
        if (!actionQueue.isEmpty()) {
            actionQueue.get(0).stop();
        }

        actionQueue.clear();
        addAction(action);
    }

    public void addAction(Action<?> action) {
        actionQueue.add(action);
    }

    public void addActionOnStart(Action<?> action) {
        if (!actionQueue.isEmpty()) {
            actionQueue.get(0).stop();
        }

        actionQueue.add(0, action);
    }

    public boolean isEmpty() {
        return actionQueue.isEmpty();
    }

    public boolean contains(Class<? extends Action<?>> actionClass) {
        return actionQueue.stream().anyMatch(
                action -> action.isAssignableFrom(actionClass));
    }

    public boolean startsWith(Class<? extends Action<?>> actionClass) {
        return !actionQueue.isEmpty() && actionQueue.get(0).isAssignableFrom(actionClass);
    }

    @Override
    public void performDefaultAction(WithNode target) {
        if (WithMovingControl.class.isInstance(unit)) {
            setAction(new MoveTowardsTargetAction(gameState, WithMovingControl.class.cast(unit), target, 5f));
        }
    }

    @Override
    public void performDefaultAction(Vector2f target) {
        if (WithMovingControl.class.isInstance(unit)) {
            setAction(new MoveTowardsLocationAction(gameState, WithMovingControl.class.cast(unit), target, 1f));
        }
    }

}
