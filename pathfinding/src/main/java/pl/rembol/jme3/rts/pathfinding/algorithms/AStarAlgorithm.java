package pl.rembol.jme3.rts.pathfinding.algorithms;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.pathfinding.ClusterBorder;
import pl.rembol.jme3.rts.pathfinding.PathfindingCluster;
import pl.rembol.jme3.geom.Rectangle2f;
import pl.rembol.jme3.geom.Vector2i;
import pl.rembol.jme3.rts.pathfinding.paths.ComplexPath;
import pl.rembol.jme3.rts.pathfinding.paths.IPath2i;
import pl.rembol.jme3.rts.pathfinding.paths.Vector2iPath;
import pl.rembol.jme3.rts.pathfinding.paths.VectorPath;

import java.util.*;
import java.util.function.Function;

public class AStarAlgorithm {

    public static class AStarComparator implements Comparator<IPath2i> {

        private Rectangle2f target;

        public AStarComparator(Rectangle2f target) {
            this.target = target;
        }

        @Override
        public int compare(IPath2i path1, IPath2i path2) {
            return Float.compare(getDistance(path1), getDistance(path2));
        }

        private float getDistance(IPath2i path) {
            return target.distance(path.getLast()) + path.getLength();
        }

    }

    public static ComplexPath buildSectorPath(Map<ClusterBorder, Vector2iPath> startingPaths, Rectangle2f target,
                                              Map<ClusterBorder, VectorPath> targetPaths, int maxIterations) {

        AStarComparator comparator = new AStarComparator(target);

        TreeSet<ComplexPath> paths = new TreeSet<>(comparator);

        for (Map.Entry<ClusterBorder, Vector2iPath> entry : startingPaths
                .entrySet()) {
            ComplexPath complexPath = new ComplexPath(entry.getValue(),
                    entry.getKey());
            paths.add(complexPath);
        }

        int iteration = 0;
        Set<Vector2i> nodesVisited = new HashSet<>();
        Set<PathfindingCluster> clustersVisited = new HashSet<>();
        while (!paths.isEmpty() && iteration++ < maxIterations) {
            ComplexPath path = paths.first();
            paths.remove(path);

            if (nodesVisited.contains(path.getLast())) {
                continue;
            }
            nodesVisited.add(path.getLast());
            if (path.getLastBorder() != null) {
                clustersVisited.add(path.getLastBorder().getCluster());
            }

            for (ClusterBorder targetBorder : targetPaths.keySet()) {
                if (targetBorder.getMiddlePoint().equals(path.getLast())) {
                    Vector2f targetPoint = targetPaths.get(targetBorder)
                            .getLast();
                    targetPaths.values().forEach(VectorPath::clearPath);

                    return new ComplexPath(new Vector2i(targetPoint), path);
                }
            }

            for (ClusterBorder neighbor : path.getLastBorder().getNeighbors()) {
                if (!nodesVisited.contains(neighbor.getMiddlePoint())) {

                    ComplexPath newPath = new ComplexPath(neighbor, path);

                    for (ClusterBorder targetBorder : targetPaths.keySet()) {
                        if (targetBorder.getMiddlePoint().equals(
                                newPath.getLast())) {
                            Vector2f targetPoint = targetPaths
                                    .get(targetBorder).getLast();

                            targetPaths.values().forEach(VectorPath::clearPath);

                            return new ComplexPath(new Vector2i(targetPoint),
                                    path);
                        }
                    }

                    if (!paths.contains(newPath)) {
                        paths.add(newPath);
                    }
                }
            }
        }
        new VectorPath(new Vector2iPath(new ArrayList<>()));

        targetPaths.values().forEach(VectorPath::clearPath);
        return null;
    }

    public static Optional<VectorPath> buildUnitPath(Vector2f start, Rectangle2f target, int maxIterations,
                                                     Function<Vector2i, Boolean> isBlockFreeFunction) {

        if (!isBlockFreeFunction.apply(new Vector2i(start))) {
            return Optional.empty();
        }

        AStarComparator comparator = new AStarComparator(target);

        TreeSet<Vector2iPath> paths = new TreeSet<>(comparator);

        Vector2iPath startingPath = new Vector2iPath(start);
        paths.add(startingPath);

        int iteration = 0;
        Set<Vector2i> nodesVisited = new HashSet<>();
        while (!paths.isEmpty() && iteration++ < maxIterations) {
            Vector2iPath path = paths.first();
            paths.remove(path);

            if (nodesVisited.contains(path.getLast())) {
                continue;
            }
            nodesVisited.add(path.getLast());

            if (target.distance(path.getLast()) == 0) {
                return Optional.of(new VectorPath(path));
            }

            for (Map.Entry<Vector2i, List<Vector2i>> neighborEntry : path
                    .getLast().getNeighbors().entrySet()) {
                if (canTraverse(isBlockFreeFunction, nodesVisited,
                        neighborEntry)) {
                    Vector2iPath newPath = new Vector2iPath(path,
                            neighborEntry.getKey());
                    if (target.distance(neighborEntry.getKey()) == 0) {
                        return Optional.of(new VectorPath(newPath));
                    }

                    if (!paths.contains(newPath)) {
                        paths.add(newPath);
                    }
                }
            }
        }

        return Optional.empty();
    }

    private static boolean canTraverse(
            Function<Vector2i, Boolean> isBlockFreeFunction,
            Set<Vector2i> nodesVisited,
            Map.Entry<Vector2i, List<Vector2i>> neighborEntry) {
        return isBlockFreeFunction.apply(neighborEntry.getKey())
                && !nodesVisited.contains(neighborEntry.getKey())
                && neighborEntry
                .getValue()
                .stream()
                .allMatch(
                        isBlockFreeFunction::apply);
    }

}
