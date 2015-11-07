package pl.rembol.jme3.world.pathfinding.paths;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.world.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VectorPath implements IExternalPath {

    private List<Vector3f> vectorList = new ArrayList<>();

    public VectorPath(GameState gameState, Vector2f... points) {
        for (Vector2f point : points) {
            vectorList.add(gameState.terrain
                    .getGroundPosition(point));
        }
    }

    public VectorPath(Vector2iPath path, GameState gameState) {
        vectorList = path.vectorList
                .stream()
                .map(vector2i -> gameState.terrain
                        .getGroundPosition(vector2i.asVector2f()))
                .collect(Collectors.toList());
    }

    public List<Vector3f> getVectorList() {
        return vectorList;
    }

    public Vector3f getLast() {
        if (vectorList.isEmpty()) {
            return null;
        }

        return vectorList.get(vectorList.size() - 1);
    }

    public void clearPath() {
        vectorList.clear();
    }

    public void updatePath(Vector3f location) {
        if (vectorList.isEmpty()) {
            return;
        }

        if (location.distance(vectorList.get(0)) < 1) {
            vectorList.remove(0);

            updatePath(location);
        }
    }

    public boolean isFinished(Vector3f location) {
        if (vectorList.isEmpty()) {
            clearPath();
            return true;
        }

        if (location.distance(getLast()) < 1) {
            clearPath();
            return true;
        }

        return false;
    }

    public Vector3f getCheckPoint() {
        if (vectorList.isEmpty()) {
            return null;
        }

        return vectorList.get(0);
    }

    public float length() {
        float length = 0;
        for (int i = 0; i < vectorList.size() - 1; ++i) {
            length += vectorList.get(i).distance(vectorList.get(i + 1));
        }

        return length;
    }

}
