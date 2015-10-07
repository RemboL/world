package pl.rembol.jme3.world.pathfinding.paths;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.world.pathfinding.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class Vector2iPath implements IPath2i {

	List<Vector2i> vectorList = new ArrayList<>();
	float length = 0;

	public Vector2iPath(Vector2f start) {
		this(new Vector2i(start));
	}

	public Vector2iPath(Vector2i start) {
		vectorList.add(start);
	}

	public Vector2iPath(Vector2iPath path, Vector2i neighbor) {
		vectorList.addAll(path.vectorList);
		vectorList.add(neighbor);

		recalculateLength();
	}

	public Vector2iPath(List<Vector2i> list) {
		vectorList.addAll(list);

		recalculateLength();
	}

	public void add(Vector2i vector) {
		vectorList.add(vector);

		recalculateLength();
	}

	public Vector2i getLast() {
		if (vectorList.isEmpty()) {
			return null;
		}

		return vectorList.get(vectorList.size() - 1);
	}

	private void recalculateLength() {
		if (vectorList.size() < 2) {
			length = 0;
			return;
		}

		length = 0;
		for (int i = 0; i < vectorList.size() - 1; ++i) {
			length += vectorList.get(i).distance(vectorList.get(i + 1));
		}
	}

	public float getLength() {
		return length;
	}

	@Override
	public boolean equals(Object that) {
		if (getLast() == null) {
			if (that instanceof Vector2iPath
					&& Vector2iPath.class.cast(that).getLast() == null) {
				return true;
			}

			if (that == null) {
				return true;
			}
			return false;
		}
		if (that instanceof Vector2iPath) {
			return getLast().equals(Vector2iPath.class.cast(that).getLast());
		}

		if (that instanceof Vector2i) {
			return getLast().equals(Vector2i.class.cast(that));
		}

		return false;
	}

	@Override
	public int hashCode() {
		if (getLast() == null) {
			return 0;
		}

		return getLast().hashCode();
	}

	public Vector3f getCheckPoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return getLast().toString();
	}

	public List<Vector2i> getVectorList() {
		return vectorList;
	}
}
