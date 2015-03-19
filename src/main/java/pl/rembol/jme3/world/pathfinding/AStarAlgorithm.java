package pl.rembol.jme3.world.pathfinding;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.world.pathfinding.Vector2iPath.AStarComparator;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

public class AStarAlgorithm {

	public static VectorPath buildPath(Vector2f start, Rectangle2f target,
			ApplicationContext applicationContext, int maxIterations,
			Function<Vector2i, Boolean> isBlockFreeFunction) {

		if (!isBlockFreeFunction.apply(new Vector2i(start))) {
			return null;
		}

		if (isDirectPathPossible(new Vector2i(start),
				new Vector2i(target.getClosest(start)), isBlockFreeFunction)) {
			Vector2iPath path = new Vector2iPath(start);
			path.add(new Vector2i(target.getClosest(start)));

			return new VectorPath(path, applicationContext,
					Collections.<Vector2i> emptySet());

		}

		AStarComparator comparator = new Vector2iPath.AStarComparator(target);

		TreeSet<Vector2iPath> paths = new TreeSet<>(comparator);

		Vector2iPath startingPath = new Vector2iPath(start);
		paths.add(startingPath);

		int iteration = 0;
		Set<Vector2i> nodesVisited = new HashSet<>();
		while (!paths.isEmpty() && iteration++ < maxIterations) {
			Vector2iPath path = paths.first();
			paths.remove(path);

			if (nodesVisited.contains(path.getLast())) {
				continue;
			}
			nodesVisited.add(path.getLast());

			if (target.distance(path.getLast()) == 0) {
				return new VectorPath(path, applicationContext, nodesVisited);
			}

			for (Vector2i neighbor : path.getLast().getNeighbors()) {
				if (isBlockFreeFunction.apply(neighbor)
						&& !nodesVisited.contains(neighbor)) {
					Vector2iPath newPath = new Vector2iPath(path, neighbor);
					if (target.distance(neighbor) == 0) {
						return new VectorPath(newPath, applicationContext,
								nodesVisited);
					}

					if (!paths.contains(newPath)) {
						paths.add(newPath);
					}
				}
			}
		}

		return null;
	}

	private static boolean isDirectPathPossible(Vector2i start, Vector2i end,
			Function<Vector2i, Boolean> isBlockFreeFunction) {

		if (!isBlockFreeFunction.apply(end)) {
			return false;
		}

		if (start.x == end.x) {
			for (int y = Math.min(start.y, end.y); y <= Math
					.max(start.y, end.y); ++y) {
				if (!isBlockFreeFunction.apply(new Vector2i(start.x, y))) {
					return false;
				}
			}
			return true;
		}

		float alpha = FastMath.abs(Float.valueOf(end.y - start.y)
				/ (end.x - start.x));
		int sgnY = FastMath.sign(end.y - start.y);
		int sgnX = FastMath.sign(end.x - start.x);

		int x = start.x;
		float error = 0;

		for (int y = start.y; y != end.y + sgnY; y += sgnY) {
			if (!isBlockFreeFunction.apply(new Vector2i(x, y))) {
				return false;
			}

			while (error <= 0.5 && x != end.x) {
				if (!isBlockFreeFunction.apply(new Vector2i(x, y))) {
					return false;
				}

				x += sgnX;
				error += alpha;
			}

			error -= 1f;
		}

		return true;
	}
}
