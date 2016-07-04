package pl.rembol.jme3.rts.pathfinding.paths;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.geom.Vector2i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VectorPath implements IExternalPath {

    private List<Vector2f> vectorList = new ArrayList<>();

    public VectorPath(Vector2f... points) {
        Collections.addAll(vectorList, points);
    }

    public VectorPath(Vector2iPath path) {
        vectorList = path.vectorList
                .stream()
                .map(Vector2i::asVector2f)
                .collect(Collectors.toList());
    }

    List<Vector2f> getVectorList() {
        return vectorList;
    }

    public Vector2f getLast() {
        if (vectorList.isEmpty()) {
            return null;
        }

        return vectorList.get(vectorList.size() - 1);
    }

    public void clearPath() {
        vectorList.clear();
    }

    public void updatePath(Vector2f location) {
        if (vectorList.isEmpty()) {
            return;
        }

        if (location.distance(vectorList.get(0)) < 1) {
            vectorList.remove(0);

            updatePath(location);
        }
    }

    public boolean isFinished(Vector2f location) {
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

    public Vector2f getCheckPoint() {
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
