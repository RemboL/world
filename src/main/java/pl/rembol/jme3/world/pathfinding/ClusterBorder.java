package pl.rembol.jme3.world.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

public class ClusterBorder {

	private PathfindingCluster cluster;

	private ClusterBorder adjacentBorder;

	private Map<ClusterBorder, Float> connectedBorders = new HashMap<>();

	private List<Vector2i> borderPoints = new ArrayList<>();

	Vector2i middlePoint = null;

	private Geometry geometry;
	private Geometry middPointGeometry;

	private List<VectorPath> paths = new ArrayList<>();

	ClusterBorder onCluster(PathfindingCluster cluster) {
		this.cluster = cluster;
		return this;
	}

	PathfindingCluster getCluster() {
		return cluster;
	}

	ClusterBorder leadsTo(ClusterBorder adjacent) {
		this.adjacentBorder = adjacent;
		connectedBorders.put(adjacent, 1f);
		return this;
	}

	ClusterBorder adjacent() {
		return adjacentBorder;
	}

	boolean hasPathTo(ClusterBorder border) {
		return connectedBorders.containsKey(border);
	}

	ClusterBorder addPath(ClusterBorder border, VectorPath path) {
		connectedBorders.put(border, path.length());

		paths.add(path);

		return this;
	}

	ClusterBorder withPoints(List<Vector2i> borderPoints) {
		this.borderPoints.clear();
		this.borderPoints.addAll(borderPoints);

		setMiddlePoint();

		return this;
	}

	private void setMiddlePoint() {
		Vector2f average = new Vector2f(0, 0);
		for (Vector2i borderPoint : borderPoints) {
			average = average.add(borderPoint.asVector2f());
		}
		average = average.divide(borderPoints.size());

		float closestToAverageDistance = 0;
		Vector2i closestToAverage = null;

		for (Vector2i borderPoint : borderPoints) {
			if (closestToAverage == null
					|| closestToAverageDistance > borderPoint.asVector2f()
							.distance(average)) {
				closestToAverage = borderPoint;
				closestToAverageDistance = borderPoint.asVector2f().distance(
						average);
			}
		}

		middlePoint = closestToAverage;
	}

	public ClusterBorder init(ApplicationContext context) {

		if (middPointGeometry == null) {
			middPointGeometry = new Geometry("dragSelect",
					new Sphere(5, 5, .5f));
			middPointGeometry.setLocalTranslation(context
					.getBean(Terrain.class)
					.getGroundPosition(middlePoint.asVector2f())
					.add(Vector3f.UNIT_Y.mult(10)));
			Material mat = new Material(context.getBean(AssetManager.class),
					"Common/MatDefs/Misc/Unshaded.j3md");
			mat.setColor("Color", new ColorRGBA(.5f, 1f, .5f, 1f));
			middPointGeometry.setMaterial(mat);

			context.getBean("rootNode", Node.class).attachChild(
					middPointGeometry);
		}

		Vector2i min = new Vector2i(borderPoints.get(0).x,
				borderPoints.get(0).y);
		Vector2i max = new Vector2i(borderPoints.get(0).x,
				borderPoints.get(0).y);

		for (Vector2i point : borderPoints) {
			min.x = Math.min(min.x, point.x);
			min.y = Math.min(min.y, point.y);
			max.x = Math.max(max.x, point.x);
			max.y = Math.max(max.y, point.y);
		}

		Vector3f min3f = new Vector3f(min.x, 2f, min.y);
		Vector3f max3f = new Vector3f(max.x, min3f.y, max.y);
		min3f = min3f.subtract(new Vector3f(.1f, .1f, .1f));
		max3f = max3f.add(new Vector3f(.1f, .1f, .1f));

		if (geometry == null) {
			geometry = new Geometry("dragSelect", new Box(
					(max3f.x - min3f.x) / 2, (max3f.y - min3f.y) / 2,
					(max3f.z - min3f.z) / 2));
			geometry.setLocalTranslation(min3f.add(max3f).mult(.5f)
					.add(Vector3f.UNIT_Y.mult(10)));
			Material mat = new Material(context.getBean(AssetManager.class),
					"Common/MatDefs/Misc/Unshaded.j3md");
			mat.setColor("Color", new ColorRGBA(.5f, 1f, .5f, 1f));
			geometry.setMaterial(mat);

			context.getBean("rootNode", Node.class).attachChild(geometry);
		}
		return this;
	}

	public void clear() {
		if (geometry != null) {
			geometry.getParent().detachChild(geometry);
			geometry = null;
		}

		for (VectorPath path : paths) {
			path.clearPath();
		}

		paths.clear();

		if (middPointGeometry != null) {
			middPointGeometry.getParent().detachChild(middPointGeometry);
			middPointGeometry = null;
		}

		for (ClusterBorder border : connectedBorders.keySet()) {
			if (border.connectedBorders.containsKey(this)) {
				border.connectedBorders.remove(this);
			}
		}

	}

}
