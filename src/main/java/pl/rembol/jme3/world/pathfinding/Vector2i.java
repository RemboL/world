package pl.rembol.jme3.world.pathfinding;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.world.pathfinding.PathfindingCluster.Direction;

import java.util.*;

import static com.jme3.math.FastMath.sqr;
import static com.jme3.math.FastMath.sqrt;
import static java.lang.Math.round;

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

    public Vector2i(Vector3f vector) {
        this.x = round(vector.x);
        this.y = round(vector.z);
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

    /**
     * Returns a map having neighboring points as keys and blocking points as
     * values. <br>
     * <br>
     * For each neighboring points in keySet algorithm needs to check each point
     * in corresponding value to ensure that there is a valid path.
     *
     * @return a map of neighbors
     */
    public Map<Vector2i, List<Vector2i>> getNeighbors() {
        Map<Vector2i, List<Vector2i>> result = new HashMap<>();

        result.put(new Vector2i(x - 2, y - 1), Arrays.asList(new Vector2i(
                x - 1, y - 1), new Vector2i(x - 1, y)));
        result.put(new Vector2i(x - 2, y + 1), Arrays.asList(new Vector2i(
                x - 1, y), new Vector2i(x - 1, y + 1)));
        result.put(new Vector2i(x - 1, y - 2), Arrays.asList(new Vector2i(
                x - 1, y - 1), new Vector2i(x, y - 1)));
        result.put(new Vector2i(x - 1, y - 1),
                Arrays.asList(new Vector2i(x - 1, y), new Vector2i(x, y - 1)));
        result.put(new Vector2i(x - 1, y), Collections.<Vector2i>emptyList());
        result.put(new Vector2i(x - 1, y + 1),
                Arrays.asList(new Vector2i(x - 1, y), new Vector2i(x, y + 1)));
        result.put(new Vector2i(x - 1, y + 2), Arrays.asList(new Vector2i(
                x - 1, y + 1), new Vector2i(x, y + 1)));
        result.put(new Vector2i(x, y - 1), Collections.<Vector2i>emptyList());
        result.put(new Vector2i(x, y + 1), Collections.<Vector2i>emptyList());
        result.put(new Vector2i(x + 1, y - 2), Arrays.asList(new Vector2i(x,
                y - 1), new Vector2i(x + 1, y - 1)));
        result.put(new Vector2i(x + 1, y - 1),
                Arrays.asList(new Vector2i(x, y - 1), new Vector2i(x + 1, y)));
        result.put(new Vector2i(x + 1, y), Collections.<Vector2i>emptyList());
        result.put(new Vector2i(x + 1, y + 1),
                Arrays.asList(new Vector2i(x, y + 1), new Vector2i(x + 1, y)));
        result.put(new Vector2i(x + 1, y + 2), Arrays.asList(new Vector2i(x,
                y + 1), new Vector2i(x + 1, y + 1)));
        result.put(new Vector2i(x + 2, y - 1), Arrays.asList(new Vector2i(
                x + 1, y - 1), new Vector2i(x + 1, y)));
        result.put(new Vector2i(x + 2, y + 1), Arrays.asList(new Vector2i(
                x + 1, y), new Vector2i(x + 1, y + 1)));

        return result;
    }

    public Vector2f asVector2f() {
        return new Vector2f(x, y);
    }

    public Vector2i move(Direction direction) {
        switch (direction) {
            case UP:
                return new Vector2i(x, y + 1);
            case DOWN:
                return new Vector2i(x, y - 1);
            case LEFT:
                return new Vector2i(x - 1, y);
            case RIGHT:
                return new Vector2i(x + 1, y);
            default:
                return null;
        }
    }

    public boolean isAdjacentTo(List<Vector2i> points) {
        for (Vector2i that : points) {
            if (isAdjacentTo(that)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdjacentTo(Vector2i that) {
        if (x == that.x && Math.abs(y - that.y) == 1) {
            return true;
        }
        if (y == that.y && Math.abs(x - that.x) == 1) {
            return true;
        }
        return false;
    }
}
