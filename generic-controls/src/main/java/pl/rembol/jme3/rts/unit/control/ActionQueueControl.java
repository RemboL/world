package pl.rembol.jme3.rts.unit.control;

import java.util.ArrayList;
import java.util.List;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.rts.unit.action.Action;
import pl.rembol.jme3.rts.unit.interfaces.WithNode;

public class ActionQueueControl<T extends WithNode> extends
        AbstractControl {

    protected T unit;

    private List<Action<?>> actionQueue = new ArrayList<>();

    protected ActionQueueControl(T unit) {
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
        actionQueue.add(action);
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

}
