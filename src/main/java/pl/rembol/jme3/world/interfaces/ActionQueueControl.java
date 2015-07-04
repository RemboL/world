package pl.rembol.jme3.world.interfaces;

import java.util.ArrayList;
import java.util.List;

import pl.rembol.jme3.world.ballman.action.Action;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public abstract class ActionQueueControl<T extends WithNode> extends
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
            } else {
            }
        } else {
            onEmptyQueue();
        }
    }

    protected abstract void onFinishedAction();

    protected abstract void onEmptyQueue();

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

}
