package pl.rembol.jme3.rts.pathfinding;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.rts.pathfinding.PathfindingCluster.Direction;
import pl.rembol.jme3.rts.pathfinding.algorithms.AStarAlgorithm;
import pl.rembol.jme3.rts.pathfinding.algorithms.BresenhamAlgorithm;
import pl.rembol.jme3.rts.pathfinding.algorithms.DijkstraAlgorithm;
import pl.rembol.jme3.rts.pathfinding.paths.*;
import pl.rembol.jme3.rts.terrain.Terrain;
import pl.rembol.jme3.rts.threads.Executor;
import pl.rembol.jme3.rts.threads.ThreadManager;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class PathfindingService {

    private static final float TERRAIN_DIFF_THRESHOLD = 2f;

    public static final int MAX_PATHFINDING_ITERATIONS = 1000000;

    private Map<Integer, Map<Integer, PathfindingCluster>> clusters = new HashMap<>();
    private Terrain terrain;
    private ThreadManager threadManager;

    public PathfindingService(Terrain terrain, ThreadManager threadManager) {
        this.terrain = terrain;
        this.threadManager = threadManager;
    }

    public void initFromTerrain() {
        clusters.clear();

        for (int x = -terrain.getTerrainSize(); x <= terrain.getTerrainSize(); ++x) {
            for (int y = -terrain.getTerrainSize(); y <= terrain
                    .getTerrainSize(); ++y) {
                float height = terrain.getGroundPosition(new Vector2f(x, y)).y;

                boolean isTerrainSmooth = true;

                isTerrainSmooth &= checkTerrainSmooth(x - 1, y, height);
                isTerrainSmooth &= checkTerrainSmooth(x + 1, y, height);
                isTerrainSmooth &= checkTerrainSmooth(x, y - 1, height);
                isTerrainSmooth &= checkTerrainSmooth(x, y + 1, height);

                setBlock(x, y, isTerrainSmooth);
            }
        }

        connectClusters();
        initBorders();
    }

    private void connectClusters() {
        for (Integer x : clusters.keySet()) {
            for (Integer y : clusters.get(x).keySet()) {
                PathfindingCluster currentCluster = clusters.get(x).get(y);

                currentCluster
                        .setNeighbor(Direction.LEFT, getCluster(x - 1, y));
                currentCluster.setNeighbor(Direction.RIGHT,
                        getCluster(x + 1, y));
                currentCluster.setNeighbor(Direction.UP, getCluster(x, y + 1));
                currentCluster
                        .setNeighbor(Direction.DOWN, getCluster(x, y - 1));
            }
        }
    }

    private void initBorders() {
        for (Integer x : clusters.keySet()) {
            for (Integer y : clusters.get(x).keySet()) {
                clusters.get(x).get(y).initBorders();
            }
        }
    }

    private void initBordersIfNeeded() {
        for (Integer x : clusters.keySet()) {
            for (Integer y : clusters.get(x).keySet()) {
                clusters.get(x).get(y).initBordersIfNeeded();
            }
        }
    }

    private Optional<PathfindingCluster> getCluster(int x, int y) {
        if (clusters.containsKey(x) && clusters.get(x).containsKey(y)) {
            return Optional.of(clusters.get(x).get(y));
        }
        return Optional.empty();
    }

    private void setBlock(int x, int y, boolean isBlockFree) {
        getClusterByPoint(new Vector2i(x, y)).setBlock(x, y, isBlockFree);
    }

    public PathfindingCluster getClusterByPoint(Vector2i point) {
        int clusterX = point.x >= 0 ? point.x / PathfindingCluster.SIZE
                : -((PathfindingCluster.SIZE - 1 - point.x) / PathfindingCluster.SIZE);
        int clusterY = point.y >= 0 ? point.y / PathfindingCluster.SIZE
                : -((PathfindingCluster.SIZE - 1 - point.y) / PathfindingCluster.SIZE);

        if (clusters.get(clusterX) == null) {
            clusters.put(clusterX, new HashMap<>());
        }

        if (clusters.get(clusterX).get(clusterY) == null) {
            clusters.get(clusterX).put(
                    clusterY,
                    new PathfindingCluster().withOffset(new Vector2i(clusterX
                            * PathfindingCluster.SIZE, clusterY
                            * PathfindingCluster.SIZE)));
        }

        return clusters.get(clusterX).get(clusterY);
    }

    private boolean checkTerrainSmooth(int x, int y, float height) {
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

    public void addSolid(Vector3f position, float width) {
        for (int x = (int) floor(position.x - width); x <= (int) ceil(position.x
                + width); ++x) {
            for (int y = (int) floor(position.z - width); y <= (int) ceil(position.z
                    + width); ++y) {
                setBlock(x, y, false);
            }
        }

        initBordersIfNeeded();
    }

    public void removeSolid(Vector3f position, float width) {
        for (int x = Math.round(position.x - width); x <= Math.round(position.x
                + width); ++x) {
            for (int y = Math.round(position.z - width); y <= Math
                    .round(position.z + width); ++y) {
                setBlock(x, y, true);
            }
        }
    }

    public IExternalPath buildPath(Vector3f start, Rectangle2f target) {
        return buildPath(new Vector2f(start.x, start.z), target);
    }

    public IExternalPath buildPath(Vector2f start, Rectangle2f target) {

        Callable<IExternalPath> worker = () -> {
            if (BresenhamAlgorithm.isDirectPathPossible(new Vector2i(start),
                    new Vector2i(target.getClosest(start)),
                    this::isBlockFree)) {
                return new VectorPath(target.getClosest(start));

            } else {

                Map<ClusterBorder, Vector2iPath> startingPaths = buildStartingClusterPaths(start);
                Map<ClusterBorder, VectorPath> targetPaths = buildTargetPaths(target);

                ComplexPath complexPath = AStarAlgorithm.buildSectorPath(startingPaths, target, targetPaths, MAX_PATHFINDING_ITERATIONS);
                if (complexPath != null) {
                    return new SectorPath(complexPath, start, threadManager, this);
                }

                return new VectorPath();
            }

        };

        return new FuturePath(threadManager.submit(Executor.FULL_PATH, worker));

    }

    private Map<ClusterBorder, VectorPath> buildTargetPaths(Rectangle2f target) {
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
            return AStarAlgorithm.buildUnitPath(start.asVector2f(), target,
                    MAX_PATHFINDING_ITERATIONS,
                    cluster::isBlockFree);
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

    private Map<ClusterBorder, Vector2iPath> buildStartingClusterPaths(
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

    private boolean isBlockFree(Vector2i neighbor) {
        return getClusterByPoint(neighbor).isBlockFree(neighbor);
    }

}
