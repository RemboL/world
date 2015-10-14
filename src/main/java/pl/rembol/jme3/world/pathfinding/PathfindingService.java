package pl.rembol.jme3.world.pathfinding;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.world.pathfinding.PathfindingCluster.Direction;
import pl.rembol.jme3.world.pathfinding.algorithms.AStarAlgorithm;
import pl.rembol.jme3.world.pathfinding.algorithms.BresenhamAlgorithm;
import pl.rembol.jme3.world.pathfinding.algorithms.DijkstraAlgorithm;
import pl.rembol.jme3.world.pathfinding.paths.ComplexPath;
import pl.rembol.jme3.world.pathfinding.paths.FuturePath;
import pl.rembol.jme3.world.pathfinding.paths.IExternalPath;
import pl.rembol.jme3.world.pathfinding.paths.SectorPath;
import pl.rembol.jme3.world.pathfinding.paths.Vector2iPath;
import pl.rembol.jme3.world.pathfinding.paths.VectorPath;
import pl.rembol.jme3.world.terrain.Terrain;
import pl.rembol.jme3.world.threads.Executor;
import pl.rembol.jme3.world.threads.ThreadManager;

@Component
public class PathfindingService implements ApplicationContextAware {

	private static final float TERRAIN_DIFF_THRESHOLD = 2f;

	public static final int MAX_PATHFINDING_ITERATIONS = 1000000;

	@Autowired
	private Terrain terrain;

	@Autowired
	private ThreadManager threadManager;

	private Map<Integer, Map<Integer, PathfindingCluster>> clusters = new HashMap<>();

	private ApplicationContext applicationContext;

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
				clusters.get(x).get(y).initBorders(applicationContext);
			}
		}
	}

	private void initBordersIfNeeded() {
		for (Integer x : clusters.keySet()) {
			for (Integer y : clusters.get(x).keySet()) {
				clusters.get(x).get(y).initBordersIfNeeded(applicationContext);
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
		getClusterByPoint(new Vector2i(x, y)).setBlock(x, y, isBlockFree,
				applicationContext);
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

		PathfindingCluster cluster = clusters.get(clusterX).get(clusterY);
		return cluster;
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
					vector -> isBlockFree(vector))) {
				return new VectorPath(applicationContext,
						target.getClosest(start));

			} else {

				Map<ClusterBorder, Vector2iPath> startingPaths = buildStartingClusterPaths(start);
				Map<ClusterBorder, VectorPath> targetPaths = buildTargetPaths(target);

				ComplexPath complexPath = AStarAlgorithm.buildSectorPath(start,
						startingPaths, target, targetPaths, applicationContext,
						MAX_PATHFINDING_ITERATIONS);
				if (complexPath != null) {
					return new SectorPath(complexPath, start,
							applicationContext);
				}

				return new VectorPath(applicationContext);
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
				vector -> cluster.isBlockFree(vector))) {
			Vector2iPath path = new Vector2iPath(start);
			path.add(new Vector2i(target.getClosest(start.asVector2f())));

			return Optional.of(new VectorPath(path, applicationContext));

		} else {
			return AStarAlgorithm.buildUnitPath(start.asVector2f(), target,
					applicationContext, MAX_PATHFINDING_ITERATIONS,
					vector -> cluster.isBlockFree(vector));
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
								.map(border -> border.getMiddlePoint())
								.collect(Collectors.toList()),
						applicationContext,
						vector2i -> startingCluster.isBlockFree(vector2i));

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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
