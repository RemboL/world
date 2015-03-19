package pl.rembol.jme3.world.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.context.ApplicationContext;

public class PathfindingCluster {

	public static final int SIZE = 16;

	private static final int MAX_PATHFINDING_ITERATIONS = 1000;

	static enum Direction {
		UP, //
		DOWN, //
		LEFT, //
		RIGHT;

		Direction opposite() {
			switch (this) {
			case UP:
				return DOWN;
			case DOWN:
				return UP;
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;
			default:
				return null;
			}
		}
	}

	private Vector2i offset;

	private Map<Integer, Map<Integer, PathfindingBlock>> blocks = new HashMap<>();

	private Map<Direction, Boolean> borderInitialized = new HashMap<>();

	private List<ClusterBorder> borders = new ArrayList<>();

	private Map<Direction, PathfindingCluster> neighbouringClusters = new HashMap<>();

	private boolean isClusterInitialized = false;

	PathfindingCluster withOffset(Vector2i offset) {
		this.offset = offset;
		return this;
	}

	boolean isBorderInitialized(Direction direction) {
		return borderInitialized.get(direction) != null
				&& borderInitialized.get(direction);
	}

	void setNeighbor(Direction direction, Optional<PathfindingCluster> cluster) {
		if (cluster.isPresent()) {
			neighbouringClusters.put(direction, cluster.get());
		}
	}

	boolean isBlockFree(Vector2i vector) {
		return isBlockFree(vector.x, vector.y);
	}

	boolean isBlockFree(int x, int y) {
		if (!blocks.containsKey(x)) {
			return false;
		}

		if (!blocks.get(x).containsKey(y)) {
			return false;
		}

		return blocks.get(x).get(y).isFree();
	}

	void setBlock(int x, int y, boolean isBlockFree) {
		if (isClusterInitialized) {
			clearBorders();
		}

		if (x < offset.x || x >= offset.x + SIZE || y < offset.y
				|| y >= offset.y + SIZE) {
			return;
		}

		if (blocks.get(x) == null) {
			blocks.put(x, new HashMap<>());
		}

		if (blocks.get(x).get(y) == null) {
			blocks.get(x).put(y, new PathfindingBlock());
		}

		blocks.get(x).get(y).setFree(isBlockFree);
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
			bordersToClear.forEach(border -> border.clear());

			borders.removeAll(bordersToClear);
		}

	}

	void initBordersIfNeeded(ApplicationContext context) {
		for (Direction direction : Direction.values()) {
			if (!isBorderInitialized(direction)) {
				doInitBorder(context, direction);
			}
		}

	}

	void initBorders(ApplicationContext context) {
		for (Direction direction : Direction.values()) {
			doInitBorder(context, direction);
		}

		isClusterInitialized = true;
	}

	private void doInitBorder(ApplicationContext context, Direction direction) {
		if (!neighbouringClusters.containsKey(direction)) {
			borderInitialized.put(direction, true);
		} else {
			PathfindingCluster neighbor = neighbouringClusters.get(direction);
			List<ClusterBorder> newNeighborBorders = new ArrayList<>();
			List<ClusterBorder> newBorders = new ArrayList<>();

			List<Vector2i> borderingPoints = getBorderPoints(direction);

			borderingPoints = borderingPoints
					.stream()
					.filter(vector2i -> isBlockFree(vector2i))
					.filter(vector2i -> neighbor.isBlockFree(vector2i
							.move(direction))).collect(Collectors.toList());

			while (!borderingPoints.isEmpty()) {
				List<Vector2i> connectedBorderingPoints = extractConnectingSet(borderingPoints);

				ClusterBorder thisBorder = new ClusterBorder().onCluster(this)
						.withPoints(connectedBorderingPoints).init(context);
				ClusterBorder thatBorder = new ClusterBorder()
						.onCluster(neighbor)
						.withPoints(
								connectedBorderingPoints.stream()
										.map(point -> point.move(direction))
										.collect(Collectors.toList()))
						.init(context);
				thisBorder.leadsTo(thatBorder);
				thatBorder.leadsTo(thisBorder);

				newNeighborBorders.add(thatBorder);
				newBorders.add(thisBorder);
			}

			setBorders(direction, newBorders, context);
			neighbor.setBorders(direction.opposite(), newNeighborBorders,
					context);
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

	void setBorders(Direction direction, List<ClusterBorder> borders,
			ApplicationContext applicationContext) {
		doClearBorder(direction);

		this.borders.addAll(borders);

		for (ClusterBorder startBorder : borders) {
			for (ClusterBorder targetBorder : this.borders) {
				if (startBorder != targetBorder
						&& !startBorder.hasPathTo(targetBorder)) {

					VectorPath path = AStarAlgorithm.buildPath(
							startBorder.middlePoint.asVector2f(),
							new Rectangle2f(targetBorder.middlePoint
									.asVector2f()), applicationContext,
							MAX_PATHFINDING_ITERATIONS,
							vector -> isBlockFree(vector));
					if (path != null) {
						startBorder.addPath(targetBorder, path);
					}
				}
			}
		}

		borderInitialized.put(direction, true);

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
			return null;
		}
	}

}
