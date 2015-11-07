package pl.rembol.jme3.world.pathfinding.paths;

import com.jme3.math.Vector3f;

public interface IExternalPath {

    void updatePath(Vector3f location);

    Vector3f getCheckPoint();

    void clearPath();

    boolean isFinished(Vector3f location);

}
