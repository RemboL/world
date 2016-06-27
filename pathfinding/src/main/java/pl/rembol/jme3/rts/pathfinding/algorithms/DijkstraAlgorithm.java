package pl.rembol.jme3.rts.pathfinding.algorithms;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.geom.Vector2i;
import pl.rembol.jme3.rts.pathfinding.paths.Vector2iPath;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;

public class DijkstraAlgorithm {

    private static class PointerPath {

        private Vector2i point;

        private PointerPath rest;

        PointerPath(Vector2i point, PointerPath rest) {
            this.point = point;
            this.rest = rest;
        }

        PointerPath(Vector2i point) {
            this.point = point;
            this.rest = null;
        }

        float length() {
            if (rest == null) {
                return 0;
            }

            return point.distance(rest.point) + rest.length();
        }

        Vector2iPath toVector2iPath() {
            return new Vector2iPath(toVector2iList());
        }

        List<Vector2i> toVector2iList() {
            List<Vector2i> list = rest == null ? new ArrayList<>() : rest
                    .toVector2iList();
            list.add(point);
            return list;
        }
    }

    public static Map<Vector2i, Vector2iPath> buildUnitPath(Vector2f start,
                                                            List<Vector2i> targets,
                                                            Function<Vector2i, Boolean> isBlockFreeFunction) {

        Comparator<PointerPath> comparator = (path1, path2) -> Float.compare(
                path1.length(), path2.length());

        TreeSet<PointerPath> paths = new TreeSet<>(comparator);
        Map<Vector2i, PointerPath> pathMap = new HashMap<>();

        PointerPath startingPath = new PointerPath(new Vector2i(start));
        paths.add(startingPath);
        pathMap.put(new Vector2i(start), startingPath);

        while (!paths.isEmpty() && !pathMap.keySet().containsAll(targets)) {
            PointerPath path = paths.first();
            paths.remove(path);

            for (Map.Entry<Vector2i, List<Vector2i>> neighborEntry : path.point
                    .getNeighbors().entrySet()) {
                if (canTraverse(isBlockFreeFunction, pathMap, neighborEntry)) {

                    PointerPath newPath = new PointerPath(
                            neighborEntry.getKey(), path);
                    paths.add(newPath);
                    pathMap.put(neighborEntry.getKey(), newPath);
                }
            }
        }

        Map<Vector2i, Vector2iPath> resultMap = new HashMap<>();

        for (Vector2i target : targets) {
            if (pathMap.containsKey(target)) {
                resultMap.put(target,
                        pathMap.get(target).toVector2iPath());
            }
        }

        return resultMap;
    }

    private static boolean canTraverse(
            Function<Vector2i, Boolean> isBlockFreeFunction,
            Map<Vector2i, PointerPath> pathMap,
            Map.Entry<Vector2i, List<Vector2i>> neighborEntry) {
        return isBlockFreeFunction.apply(neighborEntry.getKey())
                && !pathMap.containsKey(neighborEntry.getKey())
                && neighborEntry
                .getValue()
                .stream()
                .allMatch(isBlockFreeFunction::apply);
    }

}
