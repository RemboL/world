package pl.rembol.jme3.world.pathfinding;

import static com.jme3.math.FastMath.sqr;
import static com.jme3.math.FastMath.sqrt;
import static java.lang.Math.round;

import java.util.Arrays;
import java.util.List;

import com.jme3.math.Vector2f;

public class Vector2i {

	public int x;
	public int y;

	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vector2i(Vector2f vector) {
		this.x = round(vector.x);
		this.y = round(vector.y);
	}

	public float distance(Vector2i that) {
		return sqrt(sqr(that.x - x) + sqr(that.y - y));
	}

	@Override
	public boolean equals(Object that) {
		if (that == null) {
			return false;
		}
		if (!(that instanceof Vector2i)) {
			return false;
		}

		return Vector2i.class.cast(that).x == x
				&& Vector2i.class.cast(that).y == y;
	}

	@Override
	public int hashCode() {
		return x << 4 + y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public List<Vector2i> getNeighbors() {
		return Arrays.asList( //
				new Vector2i(x - 2, y - 1), //
				new Vector2i(x - 2, y + 1), //
				new Vector2i(x - 1, y - 2), //
				new Vector2i(x - 1, y - 1), //
				new Vector2i(x - 1, y), //
				new Vector2i(x - 1, y + 1), //
				new Vector2i(x - 1, y + 2), //
				new Vector2i(x, y - 1), //
				new Vector2i(x, y + 1), //
				new Vector2i(x + 1, y - 2), //
				new Vector2i(x + 1, y - 1), //
				new Vector2i(x + 1, y), //
				new Vector2i(x + 1, y + 1), //
				new Vector2i(x + 1, y + 2), //
				new Vector2i(x + 2, y - 1), //
				new Vector2i(x + 2, y + 1) //
				);
	}

	public Vector2f asVector2f() {
		return new Vector2f(x, y);
	}

}
