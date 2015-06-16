package pl.rembol.jme3.world.pathfinding.paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.world.pathfinding.PathfindingCluster;
import pl.rembol.jme3.world.pathfinding.PathfindingService;
import pl.rembol.jme3.world.pathfinding.Rectangle2f;
import pl.rembol.jme3.world.pathfinding.Vector2i;
import pl.rembol.jme3.world.pathfinding.algorithms.AStarAlgorithm;
import pl.rembol.jme3.world.pathfinding.algorithms.BresenhamAlgorithm;
import pl.rembol.jme3.world.terrain.Terrain;
import pl.rembol.jme3.world.threads.Executor;
import pl.rembol.jme3.world.threads.ThreadManager;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class SectorPath implements IExternalPath {

    private static class FarStep {
        Vector3f point;
        PathfindingCluster cluster;
        VectorPath path = null;
        Future<VectorPath> pathWorker = null;

        public FarStep(Vector3f point, PathfindingCluster cluster) {
            this.point = point;
            this.cluster = cluster;
        }

    }

    private List<FarStep> farList = new ArrayList<>();
    private int stepCounter = 0;

    private ApplicationContext applicationContext;

    public SectorPath(ComplexPath vectorPath, Vector2f start,
            ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
        PathfindingCluster lastCluster = null;
        Vector2i lastClusterPoint = null;

        for (Vector2i point : vectorPath.toVector2iList()) {
            PathfindingCluster currentCluster = applicationContext.getBean(
                    PathfindingService.class).getClusterByPoint(point);

            if (lastCluster == null) {
                lastCluster = currentCluster;
                lastClusterPoint = point;
            } else if (lastCluster != null && currentCluster == lastCluster) {
                lastClusterPoint = point;
            } else if (lastCluster != null && currentCluster != lastCluster) {
                farList.add(new FarStep(applicationContext.getBean(
                        Terrain.class).getGroundPosition(
                        lastClusterPoint.asVector2f()), lastCluster));

                lastCluster = currentCluster;
                lastClusterPoint = point;
            }
        }

        farList.add(new FarStep(applicationContext.getBean(Terrain.class)
                .getGroundPosition(lastClusterPoint.asVector2f()), lastCluster));

        if (farList.size() > 0) {
            try {
                farList.get(0).path = createStepWorker(new Vector2i(start), 0)
                        .call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void addStepWorker(Vector2i start, final int step) {
        if (step < 0 || step >= farList.size()) {
            return;
        }

        if (farList.get(step) == null) {
            return;
        }

        if (farList.get(step).path != null
                || farList.get(step).pathWorker != null) {
            return;
        }

        Callable<VectorPath> caller = createStepWorker(start, step);

        farList.get(step).pathWorker = applicationContext.getBean(
                ThreadManager.class).submit(Executor.PARTIAL_PATH, caller);
    }

    private Callable<VectorPath> createStepWorker(Vector2i start, final int step) {
        return new Callable<VectorPath>() {

            @Override
            public VectorPath call() {
                Function<Vector2i, Boolean> isFree;
                Vector2i target;

                if (step + 1 < farList.size()) {
                    isFree = vector2i -> (start.equals(vector2i)
                            || farList.get(step).cluster.isBlockFree(vector2i) || farList
                            .get(step + 1).cluster.isBlockFree(vector2i));

                    target = new Vector2i(farList.get(step + 1).point);
                } else {
                    isFree = vector2i -> (start.equals(vector2i) || farList
                            .get(step).cluster.isBlockFree(vector2i));
                    target = new Vector2i(farList.get(step).point);
                }

                if (BresenhamAlgorithm.isDirectPathPossible(start, target,
                        isFree)) {
                    Vector2f delta = target.asVector2f()
                            .subtract(start.asVector2f()).normalize();

                    if (step + 1 < farList.size()) {
                        for (Vector2f vector = start.asVector2f(); vector
                                .distance(target.asVector2f()) >= 1; vector = vector
                                .add(delta)) {
                            if (farList.get(step + 1).cluster
                                    .isBlockFree(new Vector2i(vector))) {
                                return new VectorPath(new Vector2iPath(
                                        Arrays.asList(start, new Vector2i(
                                                vector))), applicationContext);
                            }

                        }

                    }
                    return new VectorPath(new Vector2iPath(Arrays.asList(start,
                            target)), applicationContext);

                }

                Optional<VectorPath> path = AStarAlgorithm.buildUnitPath(
                        start.asVector2f(),
                        new Rectangle2f(target.asVector2f()),
                        applicationContext,
                        PathfindingService.MAX_PATHFINDING_ITERATIONS, isFree);

                if (path.isPresent()) {
                    if (step + 1 < farList.size()) {
                        List<Vector2i> newList = new ArrayList<>();

                        for (Vector3f vector : path.get().getVectorList()) {
                            newList.add(new Vector2i(vector));
                            if (farList.get(step + 1).cluster
                                    .isBlockFree(new Vector2i(vector))) {
                                path.get().clearPath();
                                return new VectorPath(
                                        new Vector2iPath(newList),
                                        applicationContext);
                            }
                        }
                    }

                    return new VectorPath(new Vector2iPath(path.get().getVectorList()
                            .stream().map(vector -> new Vector2i(vector))
                            .collect(Collectors.toList())), applicationContext);
                }

                return null;
            }
        };

    }

    @Override
    public void updatePath(Vector3f location) {
        checkWorkers();

        if (stepCounter >= farList.size()) {
            return;
        }

        if (farList.get(stepCounter).pathWorker == null) {
            addStepWorker(new Vector2i(location), stepCounter);
        }

        if (farList.get(stepCounter).path != null) {
            farList.get(stepCounter).path.updatePath(location);

            if (farList.get(stepCounter).path.isFinished(location)) {
                stepCounter++;
            }
        }

        if (stepCounter + 1 < farList.size()) {
            if (farList.get(stepCounter + 1).path == null
                    && farList.get(stepCounter + 1).pathWorker == null
                    && farList.get(stepCounter).path != null) {
                addStepWorker(
                        new Vector2i(farList.get(stepCounter).path.getLast()),
                        stepCounter + 1);
            }
        }

    }

    private void checkWorkers() {
        for (FarStep step : farList) {
            if (step.path == null && step.pathWorker != null
                    && step.pathWorker.isDone()) {
                try {
                    step.path = step.pathWorker.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Vector3f getCheckPoint() {
        if (stepCounter >= farList.size()) {
            return null;
        }

        if (farList.get(stepCounter).path != null) {
            return farList.get(stepCounter).path.getCheckPoint();
        }

        return null;
    }

    @Override
    public void clearPath() {
        for (FarStep step : farList) {
            if (step.pathWorker != null) {
                if (step.pathWorker.isDone()) {
                    try {
                        VectorPath vectorPath = step.pathWorker.get();
                        if (vectorPath != null) {
                            vectorPath.clearPath();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                } else {

                    step.pathWorker.cancel(true);
                }
            }

            if (step.path != null) {
                step.path.clearPath();
            }
        }

    }

    @Override
    public boolean isFinished(Vector3f location) {
        if (stepCounter >= farList.size()) {
            clearPath();
            return true;
        }

        return false;
    }

}
