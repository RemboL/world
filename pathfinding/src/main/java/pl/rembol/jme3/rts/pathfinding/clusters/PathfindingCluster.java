package pl.rembol.jme3.rts.pathfinding.clusters;

import pl.rembol.jme3.geom.Direction;
import pl.rembol.jme3.geom.Rectangle2f;
import pl.rembol.jme3.geom.Vector2i;
import pl.rembol.jme3.rts.pathfinding.algorithms.BresenhamAlgorithm;
import pl.rembol.jme3.rts.pathfinding.algorithms.UnitAStarAlgorithm;
import pl.rembol.jme3.rts.pathfinding.paths.Vector2iPath;
import pl.rembol.jme3.rts.pathfinding.paths.VectorPath;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class PathfindingCluster {

    static final int SIZE = 16;

    private static final int MAX_PATHFINDING_ITERATIONS = 1000;

    private Vector2i offset;

    private Map<Vector2i, PathfindingBlock> blocks = new HashMap<>();

    private Map<Direction, Boolean> borderInitialized = new HashMap<>();

    private List<ClusterBorder> borders = new ArrayList<>();

    private Map<Direction, PathfindingCluster> neighbouringClusters = new HashMap<>();

    private boolean isClusterInitialized = false;

    PathfindingCluster withOffset(Vector2i offset) {
        this.offset = offset;
        return this;
    }

    private boolean isBorderInitialized(Direction direction) {
        return borderInitialized.get(direction) != null
                && borderInitialized.get(direction);
    }

    void setNeighbor(Direction direction, PathfindingCluster cluster) {
        if (cluster != null) {
            neighbouringClusters.put(direction, cluster);
        }
    }

    List<ClusterBorder> getBorders() {
        return borders;
    }

    boolean isBlockFree(Vector2i vector) {
        return blocks.containsKey(vector) && blocks.get(vector).isFree();

    }

    void setBlock(Vector2i vector, boolean isBlockFree) {
        if (isClusterInitialized) {
            clearBorders();
        }

        if (vector.x < offset.x || vector.x >= offset.x + SIZE || vector.y < offset.y
                || vector.y >= offset.y + SIZE) {
            return;
        }

        blocks.putIfAbsent(vector, new PathfindingBlock());
        blocks.get(vector).setFree(isBlockFree);
    }

    private void clearBorders() {
        for (Direction direction : Direction.values()) {
            clearBorder(direction);
        }
    }

    private void clearBorder(Direction direction) {
        doClearBorder(direction);
        borderInitialized.put(direction, false);

        neighbouringClusters.get(direction).doClearBorder(direction.opposite());
        neighbouringClusters.get(direction).borderInitialized.put(
                direction.opposite(), false);
    }

    private void doClearBorder(Direction direction) {
        if (isBorderInitialized(direction)) {
            List<ClusterBorder> bordersToClear = borders
                    .stream()
                    .filter(border -> border.adjacent().getCluster() == neighbouringClusters
                            .get(direction)).collect(Collectors.toList());
            bordersToClear.forEach(ClusterBorder::clear);

            borders.removeAll(bordersToClear);
        }

    }

    void initBordersIfNeeded() {
        for (Direction direction : Direction.values()) {
            if (!isBorderInitialized(direction)) {
                doInitBorder(direction);
            }
        }

    }

    void initBorders() {
        for (Direction direction : Direction.values()) {
            doInitBorder(direction);
        }

        isClusterInitialized = true;
    }

    private void doInitBorder(Direction direction) {
        if (!neighbouringClusters.containsKey(direction)) {
            borderInitialized.put(direction, true);
        } else {
            PathfindingCluster neighbor = neighbouringClusters.get(direction);
            List<ClusterBorder> newNeighborBorders = new ArrayList<>();
            List<ClusterBorder> newBorders = new ArrayList<>();

            List<Vector2i> borderingPoints = getBorderPoints(direction);

            borderingPoints = borderingPoints
                    .stream()
                    .filter(this::isBlockFree)
                    .filter(vector2i -> neighbor.isBlockFree(vector2i
                            .move(direction))).collect(Collectors.toList());

            while (!borderingPoints.isEmpty()) {
                List<Vector2i> connectedBorderingPoints = extractConnectingSet(borderingPoints);

                ClusterBorder thisBorder = new ClusterBorder().onCluster(this)
                        .withPoints(connectedBorderingPoints).init();
                ClusterBorder thatBorder = new ClusterBorder()
                        .onCluster(neighbor)
                        .withPoints(
                                connectedBorderingPoints.stream()
                                        .map(point -> point.move(direction))
                                        .collect(Collectors.toList()))
                        .init();
                thisBorder.leadsTo(thatBorder);
                thatBorder.leadsTo(thisBorder);

                newNeighborBorders.add(thatBorder);
                newBorders.add(thisBorder);
            }

            setBorders(direction, newBorders);
            neighbor.setBorders(direction.opposite(), newNeighborBorders);
        }
    }

    private List<Vector2i> extractConnectingSet(List<Vector2i> borderingPoints) {
        List<Vector2i> connectedBorderingPoints = new ArrayList<>();
        connectedBorderingPoints.add(borderingPoints.remove(0));

        boolean modified = true;
        while (modified && !borderingPoints.isEmpty()) {
            modified = false;

            for (Vector2i point : borderingPoints) {
                if (point.isAdjacentTo(connectedBorderingPoints)) {
                    connectedBorderingPoints.add(point);
                    borderingPoints.remove(point);
                    modified = true;
                    break;
                }
            }

        }
        return connectedBorderingPoints;
    }

    private void setBorders(Direction direction, List<ClusterBorder> borders) {
        doClearBorder(direction);

        this.borders.addAll(borders);

        for (ClusterBorder startBorder : borders) {
            for (ClusterBorder targetBorder : this.borders) {
                connectBorders(startBorder, targetBorder);
                connectBorders(targetBorder, startBorder);
            }
        }

        borderInitialized.put(direction, true);

    }

    private void connectBorders(ClusterBorder startBorder, ClusterBorder targetBorder) {
        if (startBorder != targetBorder && !startBorder.hasPathTo(targetBorder)) {

            if (BresenhamAlgorithm.isDirectPathPossible(
                    startBorder.middlePoint, targetBorder.middlePoint,
                    this::isBlockFree)) {
                Vector2iPath path = new Vector2iPath(startBorder.middlePoint);
                path.add(targetBorder.middlePoint);

                startBorder.addPath(targetBorder, new VectorPath(path));

            } else {
                Vector2iPath path = new UnitAStarAlgorithm(
                        startBorder.middlePoint.asVector2f(),
                        new Rectangle2f(targetBorder.middlePoint.asVector2f()),
                        this::isBlockFree)
                        .buildPath();
                if (path != null) {
                    startBorder.addPath(targetBorder, new VectorPath(path));
                }
            }
        }
    }

    private List<Vector2i> getBorderPoints(Direction direction) {
        switch (direction) {
            case UP:
                return IntStream.range(offset.x, offset.x + SIZE).boxed()
                        .map(x -> new Vector2i(x, offset.y + SIZE - 1))
                        .collect(Collectors.toList());
            case DOWN:
                return IntStream.range(offset.x, offset.x + SIZE).boxed()
                        .map(x -> new Vector2i(x, offset.y))
                        .collect(Collectors.toList());
            case LEFT:
                return IntStream.range(offset.y, offset.y + SIZE).boxed()
                        .map(y -> new Vector2i(offset.x, y))
                        .collect(Collectors.toList());
            case RIGHT:
                return IntStream.range(offset.y, offset.y + SIZE).boxed()
                        .map(y -> new Vector2i(offset.x + SIZE - 1, y))
                        .collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

    public String toString() {
        int minX = blocks.keySet().stream().map(vector2i -> vector2i.x)
                .reduce(999, Math::min);
        int maxX = blocks.keySet().stream().map(vector2i -> vector2i.x)
                .reduce(-999, Math::max);
        int minY = blocks.keySet().stream().map(vector2i -> vector2i.y)
                .reduce(999, Math::min);
        int maxY = blocks.keySet().stream().map(vector2i -> vector2i.y)
                .reduce(-999, Math::max);

        return "(" + minX + ", " + minY + ") - (" + maxX + ", " + maxY + ")";
    }
}
