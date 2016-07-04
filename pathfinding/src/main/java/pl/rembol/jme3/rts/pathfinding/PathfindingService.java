package pl.rembol.jme3.rts.pathfinding;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.geom.Rectangle2f;
import pl.rembol.jme3.geom.Vector2i;
import pl.rembol.jme3.rts.pathfinding.algorithms.AbstractAStarAlgorithm;
import pl.rembol.jme3.rts.pathfinding.algorithms.BresenhamAlgorithm;
import pl.rembol.jme3.rts.pathfinding.algorithms.ClusterAStarAlgorithm;
import pl.rembol.jme3.rts.pathfinding.clusters.ClusterBorder;
import pl.rembol.jme3.rts.pathfinding.clusters.ClusterManager;
import pl.rembol.jme3.rts.pathfinding.paths.*;
import pl.rembol.jme3.rts.terrain.Terrain;
import pl.rembol.jme3.rts.threads.Executor;
import pl.rembol.jme3.rts.threads.ThreadManager;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class PathfindingService {

    private final ClusterManager clusterManager;

    private ThreadManager threadManager;

    public PathfindingService(ThreadManager threadManager) {
        this.threadManager = threadManager;
        this.clusterManager = new ClusterManager();
    }

    public void addSolid(Vector3f position, float width) {
        clusterManager.addSolid(position, width);
    }

    public void removeSolid(Vector3f position, float width) {
        clusterManager.removeSolid(position, width);
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

                Map<ClusterBorder, Vector2iPath> startingPaths = clusterManager.buildStartingClusterPaths(start);
                Map<ClusterBorder, VectorPath> targetPaths = clusterManager.buildTargetPaths(target);

                ComplexPath complexPath = new ClusterAStarAlgorithm(startingPaths, target, targetPaths).buildPath();
                if (complexPath != null) {
                    return new SectorPath(complexPath, start, threadManager, this);
                }

                return new VectorPath();
            }

        };

        return new FuturePath(threadManager.submit(Executor.FULL_PATH, worker));

    }

    private boolean isBlockFree(Vector2i neighbor) {
        return clusterManager.isBlockFree(neighbor);
    }

    public boolean isSameCluster(Vector2i point1, Vector2i point2) {
        return clusterManager.isSameCluster(point1, point2);
    }

    public Function<Vector2i, Boolean> getIsBlockFreeFunctionByCluster(Vector2i point) {
        return clusterManager.getIsBlockFreeFunctionByCluster(point);
    }

    public void initFromTerrain(Terrain terrain) {
        clusterManager.initFromTerrain(terrain);
    }
}
