package pl.rembol.jme3.world.pathfinding;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.world.Solid;

public class Rectangle2f {

	private Vector2f start;
	private Vector2f end;

	public Rectangle2f(Solid solid, float buffer) {
		this.start = new Vector2f(solid.getNode().getWorldTranslation().x
				- solid.getWidth() - buffer, solid.getNode()
				.getWorldTranslation().z - solid.getWidth() - buffer);
		this.end = new Vector2f(solid.getNode().getWorldTranslation().x
				+ solid.getWidth() + buffer, solid.getNode()
				.getWorldTranslation().z + solid.getWidth() + buffer);
	}

	public Rectangle2f(Vector2f start, Vector2f end) {
		this.start = new Vector2f(Math.min(start.x, end.x), Math.min(start.y,
				end.y));
		this.end = new Vector2f(Math.max(start.x, end.x), Math.max(start.y,
				end.y));
	}

	public Rectangle2f(Vector2f vector) {
		this(vector, vector);
	}

	public float distance(Vector2i from) {
		return distance(new Vector2f(from.x, from.y));
	}

	public float distance(Vector2f from) {
		float distanceX = getOneDimensionDistance(start.x, end.x, from.x);
		float distanceY = getOneDimensionDistance(start.y, end.y, from.y);

		return FastMath.sqrt(distanceX * distanceX + distanceY * distanceY);
	}

	private float getOneDimensionDistance(float start, float end, float from) {
		if (from < start) {
			return start - from;
		}

		if (start <= from && from <= end) {
			return 0;
		}

		return from - end;
	}

	private float getOneDimensionClosest(float start, float end, float from) {
		if (from < start) {
			return start;
		} else if (start <= from && from <= end) {
			return from;
		} else {
			return end;
		}
	}

	public Vector2f getClosest(Vector2f from) {
		return new Vector2f(getOneDimensionClosest(start.x, end.x, from.x),
				getOneDimensionClosest(start.y, end.y, from.y));
	}

	public Vector2f getStart() {
		return start;
	}

	public Vector2f getEnd() {
		return end;
	}

	public boolean isInside(Vector3f vector) {
		return (vector.x >= start.x && vector.z >= start.y && vector.x <= end.x && vector.z <= end.y);
	}

}
