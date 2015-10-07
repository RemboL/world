package pl.rembol.jme3.world.input;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.rembol.jme3.world.input.state.InputStateManager;
import pl.rembol.jme3.world.input.state.SelectionManager;
import pl.rembol.jme3.world.terrain.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Component
public class DragSelectionManager extends AbstractControl {

	@Autowired
	private InputManager inputManager;

	@Autowired
	private Camera camera;

	@Autowired
	private Terrain terrain;

	@Autowired
	private Node rootNode;

	@Autowired
	private AssetManager assetManager;

	@Autowired
	private SelectionManager selectionManager;

	@Autowired
	private InputStateManager inputStateManager;

	private Vector3f dragStart;

	private Vector3f dragStop;

	private Geometry geometry;

	@Override
	protected void controlUpdate(float paramFloat) {
		if (geometry != null && dragStart != null) {
			Vector3f dragCurrent = getCollisionWithTerrain();
			if (dragCurrent != null) {
				this.dragStop = dragCurrent;
				geometry.setMesh(getBox(dragStart, dragCurrent));
			}
		}
	}

	@Override
	protected void controlRender(RenderManager paramRenderManager,
			ViewPort paramViewPort) {
	}

	public void start() {
		dragStart = getCollisionWithTerrain();
		if (dragStart != null) {
			dragStop = dragStart.clone();
		}
	}

	private DragSurface getBox(Vector3f dragStart, Vector3f dragStop) {
		return new DragSurface(computeVerticesArray(dragStart, dragStop));
	}

	private List<List<Vector3f>> computeVerticesArray(Vector3f dragStart,
			Vector3f dragStop) {
		float minX = min(dragStart.x, dragStop.x);
		float maxX = max(dragStart.x, dragStop.x);
		float minZ = min(dragStart.z, dragStop.z);
		float maxZ = max(dragStart.z, dragStop.z);

		List<Float> listOfX = createRange(minX, maxX);
		List<Float> listOfZ = createRange(minZ, maxZ);

		List<List<Vector3f>> result = new ArrayList<>();

		for (Float z : listOfZ) {
			List<Vector3f> vectorList = new ArrayList<>();
			for (Float x : listOfX) {
				vectorList.add(terrain.getGroundPosition(new Vector2f(x, z))
						.add(Vector3f.UNIT_Y));
			}

			result.add(vectorList);
		}

		return result;
	}

	private List<Float> createRange(float min, float max) {
		int STEP = 4;

		List<Float> list = new ArrayList<>();

		list.add(min);
		list.addAll(IntStream.rangeClosed(Math.round(min), Math.round(max))
				.filter(x -> x > min).filter(x -> x < max)
				.filter(x -> x % STEP == 0).boxed().map(i -> new Float(i))
				.collect(Collectors.toList()));
		list.add(max);

		return list;
	}

	public void cancel() {
		dragStart = null;
		dragStop = null;
		if (geometry != null) {
			rootNode.detachChild(geometry);
			geometry.removeControl(this);
		}

		geometry = null;
	}

	public void confirm() {

		if (dragStart != null && dragStop != null) {
			selectionManager.dragSelect(dragStart, dragStop);
			inputStateManager.cancelOrder();
		}

		cancel();
	}

	private Ray getClickRay() {
		Vector2f click2d = inputManager.getCursorPosition();

		Vector3f click3d = camera.getWorldCoordinates(
				new Vector2f(click2d.getX(), click2d.getY()), 0f);

		Vector3f dir = camera
				.getWorldCoordinates(
						new Vector2f(click2d.getX(), click2d.getY()), 1f)
				.subtractLocal(click3d).normalize();

		Ray ray = new Ray(click3d, dir);
		return ray;
	}

	private Vector3f getCollisionWithTerrain() {
		Ray ray = getClickRay();

		CollisionResults results = new CollisionResults();
		terrain.getTerrain().collideWith(ray, results);

		CollisionResult collision = results.getClosestCollision();

		if (collision != null) {
			return collision.getContactPoint();
		}

		return null;
	}

	public void startDragging() {
		if (geometry == null) {
			geometry = new Geometry("dragSelect", getBox(dragStart, dragStop));
			Material mat = new Material(assetManager,
					"Common/MatDefs/Misc/Unshaded.j3md");
			mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
			mat.setColor("Color", new ColorRGBA(.5f, 1f, .5f, .2f));
			geometry.setMaterial(mat);
			geometry.setQueueBucket(Bucket.Transparent);
			geometry.addControl(this);

			rootNode.attachChild(geometry);
		}
	}

}
