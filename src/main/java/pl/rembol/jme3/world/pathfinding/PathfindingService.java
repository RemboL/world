package pl.rembol.jme3.world.pathfinding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import pl.rembol.jme3.world.pathfinding.Vector2iPath.AStarComparator;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

@Component
public class PathfindingService implements ApplicationContextAware {

	private static final float TERRAIN_DIFF_THRESHOLD = 2f;

	private static final int MAX_PATHFINDING_ITERATIONS = 1000000;

	@Autowired
	private Terrain terrain;

	@Autowired
	private Node rootNode;

	private Map<Integer, Map<Integer, PathfindingBlock>> blocks = new HashMap<>();

	private ApplicationContext applicationContext;

	public void initFromTerrain() {
		blocks.clear();

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

	}

	private void setBlock(int x, int y, boolean isBlockFree) {
		if (blocks.get(x) == null) {
			blocks.put(x, new HashMap<>());
		}

		if (blocks.get(x).get(y) == null) {
			blocks.get(x).put(y, new PathfindingBlock());
		}

		blocks.get(x).get(y).setFree(isBlockFree);
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
		for (int x = Math.round(position.x - width); x <= Math.round(position.x
				+ width); ++x) {
			for (int y = Math.round(position.z - width); y <= Math
					.round(position.z + width); ++y) {
				setBlock(x, y, false);
			}
		}
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

	public VectorPath buildPath(Vector3f start, Rectangle2f target) {
		return buildPath(new Vector2f(start.x, start.z), target);
	}

	public VectorPath buildPath(Vector2f start, Rectangle2f target) {
		AStarComparator comparator = new Vector2iPath.AStarComparator(target);

		TreeSet<Vector2iPath> paths = new TreeSet<>(comparator);

		Vector2iPath startingPath = new Vector2iPath(start);
		paths.add(startingPath);

		int iteration = 0;
		Set<Vector2i> nodesVisited = new HashSet<>();
		while (!paths.isEmpty() && iteration++ < MAX_PATHFINDING_ITERATIONS) {
			Vector2iPath path = paths.first();
			paths.remove(path);
			
			if (nodesVisited.contains(path.getLast())) {
				System.out.println("#### REVISIT");
				continue;
			}
			nodesVisited.add(path.getLast());
			

			if (target.distance(path.getLast()) == 0) {
				System.out.println("returning path of length "
						+ path.getLength() + " on iteration " + iteration+", nodes visited: "+nodesVisited.size());
				return new VectorPath(path, applicationContext, nodesVisited);
			}

			for (Vector2i neighbor : path.getLast().getNeighbors()) {
				if (isBlockFree(neighbor) && !nodesVisited.contains(neighbor)) {
					Vector2iPath newPath = new Vector2iPath(path, neighbor);
					if (target.distance(neighbor) == 0) {
						System.out.println("returning path of length "
								+ path.getLength() + " on iteration " + iteration+", nodes visited: "+nodesVisited.size());
						return new VectorPath(newPath, applicationContext, nodesVisited);
					}
					
					if (!paths.contains(newPath)) {
						paths.add(newPath);
					}
				}
			}
		}

		return null;
	}

	private boolean isBlockFree(Vector2i neighbor) {
		if (!blocks.containsKey(neighbor.x)) {
			return false;
		}

		if (!blocks.get(neighbor.x).containsKey(neighbor.y)) {
			return false;
		}
		return blocks.get(neighbor.x).get(neighbor.y).isFree();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
