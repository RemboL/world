package pl.rembol.jme3.rts.pathfinding.paths;

import com.jme3.math.Vector2f;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FuturePath implements IExternalPath {

    private Future<IExternalPath> worker;
    private IExternalPath path = null;

    public FuturePath(Future<IExternalPath> worker) {
        this.worker = worker;
    }

    @Override
    public void updatePath(Vector2f location) {
        if (path != null) {
            path.updatePath(location);
        } else if (worker != null && worker.isDone()) {
            try {
                path = worker.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                worker = null;
            }
        }
    }

    @Override
    public Vector2f getCheckPoint() {
        if (path != null) {
            return path.getCheckPoint();
        }

        return null;
    }

    @Override
    public void clearPath() {
        if (worker != null) {
            worker.cancel(true);
        }
        if (path != null) {
            path.clearPath();
        }
    }

    @Override
    public boolean isFinished(Vector2f location) {
        if (worker != null && !worker.isDone()) {
            return false;
        }
        return path != null && path.isFinished(location);
    }

}
