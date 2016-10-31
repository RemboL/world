package pl.rembol.jme3.rts.pathfinding.paths;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.geom.Rectangle2f;
import pl.rembol.jme3.geom.Vector2i;
import pl.rembol.jme3.rts.pathfinding.PathfindingService;
import pl.rembol.jme3.rts.pathfinding.algorithms.BresenhamAlgorithm;
import pl.rembol.jme3.rts.pathfinding.algorithms.UnitAStarAlgorithm;
import pl.rembol.jme3.threads.Executor;
import pl.rembol.jme3.threads.ThreadManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SectorPath implements IExternalPath {

    private static class FarStep {

        Vector2f point;

        Function<Vector2i, Boolean> isBlockFreeFunction;

        VectorPath path = null;

        Future<VectorPath> pathWorker = null;

        FarStep(Vector2f point, Function<Vector2i, Boolean> isBlockFreeFunction) {
            this.point = point;
            this.isBlockFreeFunction = isBlockFreeFunction;
        }

    }

    private List<FarStep> farList = new ArrayList<>();

    private int stepCounter = 0;

    private ThreadManager threadManager;

    public SectorPath(ComplexPath vectorPath, Vector2f start, ThreadManager threadManager, PathfindingService pathfindingService) {
        this.threadManager = threadManager;
        Vector2i lastClusterPoint = null;

        for (Vector2i point : vectorPath.toVector2iList()) {
            if (lastClusterPoint == null) {
                lastClusterPoint = point;
            } else if (pathfindingService.isSameCluster(lastClusterPoint, point)) {
                lastClusterPoint = point;
            } else {
                farList.add(new FarStep(lastClusterPoint.asVector2f(), pathfindingService.getIsBlockFreeFunctionByCluster(lastClusterPoint)));

                lastClusterPoint = point;
            }
        }

        if (lastClusterPoint != null) {
            farList.add(new FarStep(lastClusterPoint.asVector2f(), pathfindingService.getIsBlockFreeFunctionByCluster(lastClusterPoint)));
        }

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

        farList.get(step).pathWorker = threadManager.submit(Executor.PARTIAL_PATH, caller);
    }

    private Callable<VectorPath> createStepWorker(Vector2i start, final int step) {
        return () -> {
            Function<Vector2i, Boolean> isFree;
            Vector2i target;

            if (step + 1 < farList.size()) {
                isFree = vector2i -> (start.equals(vector2i)
                        || farList.get(step).isBlockFreeFunction.apply(vector2i) || farList
                        .get(step + 1).isBlockFreeFunction.apply(vector2i));

                target = new Vector2i(farList.get(step + 1).point);
            } else {
                isFree = vector2i -> (start.equals(vector2i) || farList
                        .get(step).isBlockFreeFunction.apply(vector2i));
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
                        if (farList.get(step + 1).isBlockFreeFunction.apply(new Vector2i(vector))) {
                            return new VectorPath(new Vector2iPath(
                                    Arrays.asList(start, new Vector2i(
                                            vector))));
                        }

                    }

                }
                return new VectorPath(new Vector2iPath(Arrays.asList(start,
                        target)));

            }

            Vector2iPath path = new UnitAStarAlgorithm(
                    start.asVector2f(),
                    new Rectangle2f(target.asVector2f()),
                    isFree).buildPath();

            if (path != null) {
                VectorPath vectorPath = new VectorPath(path);
                if (step + 1 < farList.size()) {
                    List<Vector2i> newList = new ArrayList<>();

                    for (Vector2f vector : vectorPath.getVectorList()) {
                        newList.add(new Vector2i(vector));
                        if (farList.get(step + 1).isBlockFreeFunction.apply(new Vector2i(vector))) {
                            vectorPath.clearPath();
                            return new VectorPath(
                                    new Vector2iPath(newList));
                        }
                    }
                }

                return new VectorPath(new Vector2iPath(vectorPath.getVectorList()
                        .stream().map(Vector2i::new)
                        .collect(Collectors.toList())));
            }

            return null;
        };

    }

    @Override
    public void updatePath(Vector2f location) {
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
    public Vector2f getCheckPoint() {
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
    public boolean isFinished(Vector2f location) {
        if (stepCounter >= farList.size()) {
            clearPath();
            return true;
        }

        return false;
    }

}
