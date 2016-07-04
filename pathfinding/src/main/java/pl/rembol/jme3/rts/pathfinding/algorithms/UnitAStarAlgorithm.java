package pl.rembol.jme3.rts.pathfinding.algorithms;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.geom.Rectangle2f;
import pl.rembol.jme3.geom.Vector2i;
import pl.rembol.jme3.rts.pathfinding.paths.Vector2iPath;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UnitAStarAlgorithm extends AbstractAStarAlgorithm<Vector2iPath, Vector2i> {

    private final Rectangle2f target;
    private final Function<Vector2i, Boolean> isBlockFreeFunction;

    public UnitAStarAlgorithm(Vector2f start, Rectangle2f target,
                              Function<Vector2i, Boolean> isBlockFreeFunction) {
        this.target = target;
        this.isBlockFreeFunction = isBlockFreeFunction;
        paths = new TreeSet<>(new AStarComparator(target));

        Vector2iPath startingPath = new Vector2iPath(start);
        paths.add(startingPath);
    }

    @Override
    protected List<Vector2i> getNotVisitedNeighbors(Vector2iPath path, Set<Vector2i> nodesVisited) {
        return path
                .getLast().getNeighbors().entrySet().stream()
                .filter(neighborEntry -> canTraverse(isBlockFreeFunction, nodesVisited, neighborEntry))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    protected Vector2iPath createNewPath(Vector2i vector2i, Vector2iPath path) {
        return new Vector2iPath(path,vector2i);
    }

    @Override
    protected boolean isTargetReached(Vector2iPath path) {
        return target.distance(path.getLast()) == 0;
    }

    @Override
    protected Vector2iPath finishPath(Vector2iPath path) {
        return path;
    }

    private boolean canTraverse(
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
