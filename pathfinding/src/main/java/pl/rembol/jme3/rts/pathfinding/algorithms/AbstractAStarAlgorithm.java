package pl.rembol.jme3.rts.pathfinding.algorithms;

import pl.rembol.jme3.geom.Rectangle2f;
import pl.rembol.jme3.geom.Vector2i;
import pl.rembol.jme3.rts.pathfinding.paths.IPath2i;

import java.util.*;

public abstract class AbstractAStarAlgorithm<Path extends IPath2i, Neighbor> {

    public static final int MAX_PATHFINDING_ITERATIONS = 1000000;

    static class AStarComparator implements Comparator<IPath2i> {

        private Rectangle2f target;

        AStarComparator(Rectangle2f target) {
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

    protected TreeSet<Path> paths;

    public Path buildPath() {
        int iteration = 0;
        Set<Vector2i> nodesVisited = new HashSet<>();
        while (!paths.isEmpty() && iteration++ < MAX_PATHFINDING_ITERATIONS) {
            Path path = paths.first();
            paths.remove(path);

            if (nodesVisited.contains(path.getLast())) {
                continue;
            }
            nodesVisited.add(path.getLast());

            if (isTargetReached(path)) {
                return finishPath(path);
            }
            for (Neighbor neighbor : getNotVisitedNeighbors(path, nodesVisited)) {
                Path newPath = createNewPath(neighbor, path);

                if (isTargetReached(path)) {
                    return finishPath(path);
                    }

                    if (!paths.contains(newPath)) {
                        paths.add(newPath);
                    }
                }
            }
        return finishPath(null);
    }

    protected abstract List<Neighbor> getNotVisitedNeighbors(Path path, Set<Vector2i> nodesVisited);

    protected abstract Path createNewPath(Neighbor neighbor, Path path);

    protected abstract boolean isTargetReached(Path path);

    protected abstract Path finishPath(Path path);

}
