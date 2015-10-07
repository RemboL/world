package pl.rembol.jme3.world.pathfinding.paths;

import org.springframework.context.ApplicationContext;
import pl.rembol.jme3.world.pathfinding.ClusterBorder;
import pl.rembol.jme3.world.pathfinding.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class ComplexPath implements IPath2i {

	private Vector2i point = null;
	private ClusterBorder border = null;
	private float length;

	private ComplexPath rest = null;

	public ComplexPath(Vector2i point) {
		init(point);
	}

	public ComplexPath(ClusterBorder border) {
		init(border);
	}

	public ComplexPath(Vector2i point, ComplexPath rest) {
		if (rest == null) {
			init(point);
		} else {
			init(point, rest);
		}
	}

	public ComplexPath(ClusterBorder border, ComplexPath rest) {
		if (rest == null) {
			init(border);
		} else {
			init(border, rest);
		}
	}

	private void init(Vector2i point) {
		this.point = point;
		length = 0;
	}

	private void init(ClusterBorder border) {
		this.border = border;
		this.point = border.getMiddlePoint();
		length = 0;
	}

	private void init(Vector2i point, ComplexPath rest) {
		this.point = point;
		this.rest = rest;
		this.length = rest.point.distance(point) + rest.length;
	}

	private void init(ClusterBorder border, ComplexPath rest) {
		this.border = border;
		this.point = border.getMiddlePoint();
		this.rest = rest;
		this.length = rest.border == null ? rest.point.distance(point)
				+ rest.length : rest.border.getDistanceTo(border) + rest.length;
	}

	public ComplexPath(Vector2iPath path) {
		ComplexPath temporaryRest = null;
		for (int i = 0; i < path.vectorList.size() - 2; ++i) {
			temporaryRest = new ComplexPath(path.vectorList.get(i),
					temporaryRest);
		}

		if (temporaryRest == null) {
			init(path.getLast());
		} else {
			init(path.getLast(), temporaryRest);
		}
	}

	public ComplexPath(Vector2iPath path, ClusterBorder end) {
		ComplexPath temporaryRest = null;
		for (int i = 0; i < path.vectorList.size() - 2; ++i) {
			temporaryRest = new ComplexPath(path.vectorList.get(i),
					temporaryRest);
		}

		if (temporaryRest == null) {
			init(end);
		} else {
			init(end, temporaryRest);
		}
	}

	public float getLength() {
		return length;
	}

	public Vector2i getLast() {
		return point;
	}

	public ClusterBorder getLastBorder() {
		return border;
	}

	public VectorPath toVectorPath(ApplicationContext applicationContext) {
		return new VectorPath(toVector2iPath(), applicationContext);
	}

	public Vector2iPath toVector2iPath() {
		return new Vector2iPath(toVector2iList());
	}

	public List<Vector2i> toVector2iList() {
		List<Vector2i> vectorList = rest == null ? new ArrayList<Vector2i>()
				: rest.toVector2iList();
		vectorList.add(point);

		return vectorList;
	}

	public String toString() {
		return point.toString() + " ~ " + length;
	}

}
