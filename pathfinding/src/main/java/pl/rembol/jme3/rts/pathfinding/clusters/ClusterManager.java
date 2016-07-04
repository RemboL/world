package pl.rembol.jme3.rts.pathfinding.clusters;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.geom.Direction;
import pl.rembol.jme3.geom.Rectangle2f;
import pl.rembol.jme3.geom.Vector2i;
import pl.rembol.jme3.rts.pathfinding.PathfindingService;
import pl.rembol.jme3.rts.pathfinding.algorithms.AbstractAStarAlgorithm;
import pl.rembol.jme3.rts.pathfinding.algorithms.BresenhamAlgorithm;
import pl.rembol.jme3.rts.pathfinding.algorithms.DijkstraAlgorithm;
import pl.rembol.jme3.rts.pathfinding.algorithms.UnitAStarAlgorithm;
import pl.rembol.jme3.rts.pathfinding.paths.Vector2iPath;
import pl.rembol.jme3.rts.pathfinding.paths.VectorPath;
import pl.rembol.jme3.rts.terrain.Terrain;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class ClusterManager {

    private static final float TERRAIN_DIFF_THRESHOLD = 2f;

    private Map<Vector2i, PathfindingCluster> clusters = new HashMap<>();

    public void initFromTerrain(Terrain terrain) {
        clusters.clear();

        for (int x = -terrain.getTerrainSize(); x <= terrain.getTerrainSize(); ++x) {
            for (int y = -terrain.getTerrainSize(); y <= terrain
                    .getTerrainSize(); ++y) {
                float height = terrain.getGroundPosition(new Vector2f(x, y)).y;

                boolean isTerrainSmooth = true;

                isTerrainSmooth &= checkTerrainSmooth(terrain, x - 1, y, height);
                isTerrainSmooth &= checkTerrainSmooth(terrain, x + 1, y, height);
                isTerrainSmooth &= checkTerrainSmooth(terrain, x, y - 1, height);
                isTerrainSmooth &= checkTerrainSmooth(terrain, x, y + 1, height);

                setBlock(new Vector2i(x, y), isTerrainSmooth);
            }
        }

        connectClusters();
        initBorders();
    }

    private void connectClusters() {
        for (Vector2i point : clusters.keySet()) {
            PathfindingCluster currentCluster = clusters.get(point);

            Arrays.stream(Direction.values())
                    .forEach(direction -> currentCluster.setNeighbor(direction, getCluster(point.move(direction))));
        }
    }

    private void initBorders() {
        clusters.values().forEach(PathfindingCluster::initBorders);
    }

    private void initBordersIfNeeded() {
        clusters.values().forEach(PathfindingCluster::initBordersIfNeeded);
    }

    private PathfindingCluster getCluster(Vector2i point) {
        return clusters.get(point);
    }

    private boolean checkTerrainSmooth(Terrain terrain, int x, int y, float height) {
        if (x < -terrain.getTerrainSize() || x > terrain.getTerrainSize()) {
            return true;
        }
        if (y < -terrain.getTerrainSize() || y > terrain.getTerrainSize()) {
            return true;
        }
        if (Math.abs(terrain.getGroundPosition(new Vector2f(x, y)).y - height) > TERRAIN_DIFF_THRESHOLD) {
            return false;
        }
        return true;
    }

    private void setBlock(Vector2i point, boolean isBlockFree) {
        getClusterByPoint(point).setBlock(point, isBlockFree);
    }

    private PathfindingCluster getClusterByPoint(Vector2i point) {
        int clusterX = point.x >= 0 ? point.x / PathfindingCluster.SIZE
                : -((PathfindingCluster.SIZE - 1 - point.x) / PathfindingCluster.SIZE);
        int clusterY = point.y >= 0 ? point.y / PathfindingCluster.SIZE
                : -((PathfindingCluster.SIZE - 1 - point.y) / PathfindingCluster.SIZE);

        clusters.putIfAbsent(new Vector2i(clusterX, clusterY),
                new PathfindingCluster().withOffset(
                        new Vector2i(
                                clusterX * PathfindingCluster.SIZE,
                                clusterY * PathfindingCluster.SIZE)));

        return clusters.get(new Vector2i(clusterX, clusterY));
    }

    public void addSolid(Vector3f position, float width) {
        for (int x = (int) floor(position.x - width); x <= (int) ceil(position.x
                + width); ++x) {
            for (int y = (int) floor(position.z - width); y <= (int) ceil(position.z
                    + width); ++y) {
                setBlock(new Vector2i(x, y), false);
            }
        }

        initBordersIfNeeded();
    }

    public void removeSolid(Vector3f position, float width) {
        for (int x = Math.round(position.x - width); x <= Math.round(position.x
                + width); ++x) {
            for (int y = Math.round(position.z - width); y <= Math
                    .round(position.z + width); ++y) {
                setBlock(new Vector2i(x, y), true);
            }
        }
    }

    public Map<ClusterBorder, Vector2iPath> buildStartingClusterPaths(
            Vector2f start) {
        PathfindingCluster startingCluster = getClusterByPoint(new Vector2i(
                start));

        Map<Vector2i, Vector2iPath> startingClusterPaths = DijkstraAlgorithm
                .buildUnitPath(
                        start,
                        startingCluster.getBorders().stream()
                                .map(ClusterBorder::getMiddlePoint)
                                .collect(Collectors.toList()),
                        startingCluster::isBlockFree);

        Map<ClusterBorder, Vector2iPath> startingPaths = new HashMap<>();

        for (ClusterBorder border : startingCluster.getBorders()) {
            if (startingClusterPaths.containsKey(border.getMiddlePoint()))
                startingPaths.put(border,
                        startingClusterPaths.get(border.getMiddlePoint()));
        }
        return startingPaths;
    }

    public Map<ClusterBorder, VectorPath> buildTargetPaths(Rectangle2f target) {
        Map<ClusterBorder, VectorPath> targetPaths = new HashMap<>();

        Set<PathfindingCluster> targetClusters = getTargetClusters(target);
        for (PathfindingCluster targetCluster : targetClusters) {
            for (ClusterBorder border : targetCluster.getBorders()) {
                Optional<VectorPath> path = buildSimplePath(border.getMiddlePoint(),
                        target, targetCluster);
                if (path.isPresent()) {
                    targetPaths.put(border, path.get());
                }
            }
        }
        return targetPaths;
    }

    private Optional<VectorPath> buildSimplePath(Vector2i start, Rectangle2f target,
                                                 PathfindingCluster cluster) {
        if (BresenhamAlgorithm.isDirectPathPossible(start,
                new Vector2i(target.getClosest(start.asVector2f())),
                cluster::isBlockFree)) {
            Vector2iPath path = new Vector2iPath(start);
            path.add(new Vector2i(target.getClosest(start.asVector2f())));

            return Optional.of(new VectorPath(path));

        } else {
            return Optional.ofNullable(new UnitAStarAlgorithm(start.asVector2f(), target, cluster::isBlockFree).buildPath()).map(VectorPath::new);
        }
    }

    private Set<PathfindingCluster> getTargetClusters(Rectangle2f target) {
        Set<Integer> setX = createInclusiveSet(target.getStart().x,
                target.getEnd().x);
        Set<Integer> setY = createInclusiveSet(target.getStart().y,
                target.getEnd().y);
        Set<PathfindingCluster> targetClusters = new HashSet<>();
        for (Integer x : setX) {
            for (Integer y : setY) {
                targetClusters.add(getClusterByPoint(new Vector2i(x, y)));
            }
        }
        return targetClusters;
    }

    private Set<Integer> createInclusiveSet(float start, float end) {
        Set<Integer> set = new HashSet<>();
        for (int i = Math.round(start) + PathfindingCluster.SIZE; i < end; i += PathfindingCluster.SIZE) {
            set.add(i);
        }
        set.add(Math.round(start));
        set.add(Math.round(end));

        return set;
    }

    public boolean isBlockFree(Vector2i neighbor) {
        return getClusterByPoint(neighbor).isBlockFree(neighbor);
    }

    public boolean isSameCluster(Vector2i point1, Vector2i point2) {
        return getClusterByPoint(point1) == getClusterByPoint(point2);
    }

    public Function<Vector2i, Boolean> getIsBlockFreeFunctionByCluster(Vector2i point) {
        return getClusterByPoint(point)::isBlockFree;
    }

}
