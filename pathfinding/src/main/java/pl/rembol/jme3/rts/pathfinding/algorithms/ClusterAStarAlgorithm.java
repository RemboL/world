package pl.rembol.jme3.rts.pathfinding.algorithms;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.geom.Rectangle2f;
import pl.rembol.jme3.geom.Vector2i;
import pl.rembol.jme3.rts.pathfinding.clusters.ClusterBorder;
import pl.rembol.jme3.rts.pathfinding.paths.ComplexPath;
import pl.rembol.jme3.rts.pathfinding.paths.Vector2iPath;
import pl.rembol.jme3.rts.pathfinding.paths.VectorPath;

import java.util.*;
import java.util.stream.Collectors;

public class ClusterAStarAlgorithm extends AbstractAStarAlgorithm<ComplexPath, ClusterBorder> {

    private Map<ClusterBorder, VectorPath> targetPaths;

    public ClusterAStarAlgorithm(Map<ClusterBorder, Vector2iPath> startingPaths, Rectangle2f target,
                                 Map<ClusterBorder, VectorPath> targetPaths) {
        this.targetPaths = targetPaths;
        AStarComparator comparator = new AStarComparator(target);

        paths = new TreeSet<>(comparator);

        for (Map.Entry<ClusterBorder, Vector2iPath> entry : startingPaths
                .entrySet()) {
            ComplexPath complexPath = new ComplexPath(entry.getValue(),
                    entry.getKey());
            paths.add(complexPath);
        }
    }

    @Override
    protected List<ClusterBorder> getNotVisitedNeighbors(ComplexPath path, Set<Vector2i> nodesVisited) {
        return path.getLastBorder().getNeighbors().stream().filter(neighbor -> !nodesVisited.contains(neighbor.getMiddlePoint())).collect(Collectors.toList());
    }

    @Override
    protected ComplexPath createNewPath(ClusterBorder neighbor, ComplexPath path) {
        return new ComplexPath(neighbor, path);
    }

    @Override
    protected boolean isTargetReached(ComplexPath path) {

        for (ClusterBorder targetBorder : targetPaths.keySet()) {
            if (targetBorder.getMiddlePoint().equals(path.getLast())) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected ComplexPath finishPath(ComplexPath path) {
        if (path == null) {
            return null;
        }
        for (ClusterBorder targetBorder : targetPaths.keySet()) {
            if (targetBorder.getMiddlePoint().equals(path.getLast())) {
                Vector2f targetPoint = targetPaths.get(targetBorder)
                        .getLast();
                targetPaths.values().forEach(VectorPath::clearPath);
                return new ComplexPath(new Vector2i(targetPoint), path);
            }
        }

        return null;
    }

}
