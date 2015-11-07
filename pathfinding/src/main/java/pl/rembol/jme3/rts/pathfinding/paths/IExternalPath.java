package pl.rembol.jme3.rts.pathfinding.paths;

import com.jme3.math.Vector2f;

public interface IExternalPath {

    void updatePath(Vector2f location);

    Vector2f getCheckPoint();

    void clearPath();

    boolean isFinished(Vector2f location);

}
