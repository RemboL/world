package pl.rembol.jme3.world.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.world.DebugService;
import pl.rembol.jme3.world.pathfinding.algorithms.AStarAlgorithm;
import pl.rembol.jme3.world.pathfinding.algorithms.BresenhamAlgorithm;
import pl.rembol.jme3.world.pathfinding.paths.ComplexPath;
import pl.rembol.jme3.world.pathfinding.paths.IExternalPath;
import pl.rembol.jme3.world.pathfinding.paths.Vector2iPath;
import pl.rembol.jme3.world.pathfinding.paths.VectorPath;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class SectorPath implements IExternalPath {

	private static ExecutorService executor = Executors.newFixedThreadPool(20);

	private static class FarStep {
		Vector3f point;
		PathfindingCluster cluster;
		VectorPath path = null;
		Future<VectorPath> pathWorker = null;

		public FarStep(Vector3f point, PathfindingCluster cluster) {
			this.point = point;
			this.cluster = cluster;
		}

		@Override
		public String toString() {
			return "(point=(" + ((int) point.x) + ", " + ((int) point.z)
					+ "), cluster=" + cluster.toString() + ")";
		}
	}

	private List<FarStep> farList = new ArrayList<>();
	private int stepCounter = 0;

	private ApplicationContext applicationContext;

	SectorPath(ComplexPath vectorPath, Vector2f start,
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

		if (applicationContext.getBean(DebugService.class).getPathfinding() == 3) {
			for (int i = 0; i < farList.size(); ++i) {
				try {
					Vector2i v;
					if (i == 0) {
						v = new Vector2i(start);
					} else {
						v = new Vector2i(farList.get(i - 1).path.getLast());
					}
					farList.get(i).path = getStepWorker(v, i)
							.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			if (farList.size() > 0) {
				try {
					farList.get(0).path = getStepWorker(new Vector2i(start), 0)
							.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
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

		Callable<VectorPath> caller = getStepWorker(start, step);

		farList.get(step).pathWorker = executor.submit(caller);
	}

	private Callable<VectorPath> getStepWorker(Vector2i start, final int step) {
		return new Callable<VectorPath>() {

			@Override
			public VectorPath call() {
				Function<Vector2i, Boolean> isFree;
				Vector2i target;

				if (step + 1 < farList.size()) {
					isFree = vector2i -> (farList.get(step).cluster
							.isBlockFree(vector2i) || farList.get(step + 1).cluster
							.isBlockFree(vector2i));
					target = new Vector2i(farList.get(step + 1).point);
				} else {
					isFree = vector2i -> (farList.get(step).cluster
							.isBlockFree(vector2i));
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
												vector))), applicationContext,
										ColorRGBA.Blue);
							}

						}

					}
					return new VectorPath(new Vector2iPath(Arrays.asList(start,
							target)), applicationContext, ColorRGBA.Blue);

				}

				VectorPath path = AStarAlgorithm.buildUnitPath(
						start.asVector2f(),
						new Rectangle2f(target.asVector2f()),
						applicationContext,
						PathfindingService.MAX_PATHFINDING_ITERATIONS, isFree);

				if (path != null) {
					if (step + 1 < farList.size()) {
						List<Vector2i> newList = new ArrayList<>();

						for (Vector3f vector : path.getVectorList()) {
							newList.add(new Vector2i(vector));
							if (farList.get(step + 1).cluster
									.isBlockFree(new Vector2i(vector))) {
								path.clearPath();
								return new VectorPath(
										new Vector2iPath(newList),
										applicationContext, ColorRGBA.Blue);
							}
						}
					}

					return new VectorPath(new Vector2iPath(path.getVectorList()
							.stream().map(vector -> new Vector2i(vector))
							.collect(Collectors.toList())), applicationContext,
							ColorRGBA.Blue);
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
			if (step.pathWorker != null && step.pathWorker.isDone()) {
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
