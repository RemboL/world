package pl.rembol.jme3.world.pathfinding.paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.world.DebugService;
import pl.rembol.jme3.world.pathfinding.Vector2i;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;

public class VectorPath implements IExternalPath {

	private static Material greenMaterial = null;
	private static Material redMaterial = null;

	private List<Vector3f> vectorList = new ArrayList<>();

	private Map<Vector3f, Spatial> nodes = new HashMap<>();
	private ApplicationContext applicationContext;

	public VectorPath(Vector2iPath path, ApplicationContext applicationContext,
			ColorRGBA color) {
		this(path, applicationContext, Collections.<Vector2i> emptySet(), color);
	}

	public VectorPath(Vector2iPath path, ApplicationContext applicationContext,
			Set<Vector2i> nodesVisited) {
		this(path, applicationContext, nodesVisited, null);
	}

	public VectorPath(Vector2iPath path, ApplicationContext applicationContext) {
		this(path, applicationContext, Collections.<Vector2i> emptySet(), null);
	}

	public VectorPath(Vector2iPath path, ApplicationContext applicationContext,
			Set<Vector2i> nodesVisited, ColorRGBA color) {
		this.applicationContext = applicationContext;
		initMaterials(applicationContext.getBean(AssetManager.class));
		vectorList = path.vectorList
				.stream()
				.map(vector2i -> applicationContext.getBean(Terrain.class)
						.getGroundPosition(vector2i.asVector2f()))
				.collect(Collectors.toList());

		applicationContext.getBean(SimpleApplication.class).enqueue(
				new Callable<Void>() {

					@Override
					public Void call() {
						Material material;
						Node parent;
						if (color == null) {
							material = greenMaterial;
							parent = applicationContext.getBean(DebugService.class).getClusterPathsNode();
						} else {
							material = new Material(applicationContext
									.getBean(AssetManager.class),
									"Common/MatDefs/Misc/Unshaded.j3md");
							material.getAdditionalRenderState().setBlendMode(
									BlendMode.Alpha);
							material.setColor("Color", color);
							parent = applicationContext.getBean("rootNode", Node.class);
						}

						for (int i = 0; i < vectorList.size() - 1; ++i) {

							Geometry node = new Geometry("path node", new Line(
									vectorList.get(i).add(
											Vector3f.UNIT_Y.mult(1f)),
									vectorList.get(i + 1).add(
											Vector3f.UNIT_Y.mult(1f))));
							node.setMaterial(material);

							parent.attachChild(node);
							nodes.put(vectorList.get(i), node);
						}

						return null;
					}

				});

	}

	public List<Vector3f> getVectorList() {
		return vectorList;
	}

	private static void initMaterials(AssetManager assetManager) {
		if (greenMaterial == null) {
			greenMaterial = new Material(assetManager,
					"Common/MatDefs/Misc/Unshaded.j3md");
			greenMaterial.getAdditionalRenderState().setBlendMode(
					BlendMode.Alpha);
			greenMaterial.setColor("Color", new ColorRGBA(.5f, 1f, .5f, 1f));
		}

		if (redMaterial == null) {
			redMaterial = new Material(assetManager,
					"Common/MatDefs/Misc/Unshaded.j3md");
			redMaterial.getAdditionalRenderState()
					.setBlendMode(BlendMode.Alpha);
			redMaterial.setColor("Color", new ColorRGBA(1f, .5f, .5f, 1f));
		}
	}

	public Vector3f getLast() {
		if (vectorList.isEmpty()) {
			return null;
		}

		return vectorList.get(vectorList.size() - 1);
	}

	public void clearPath() {
		applicationContext.getBean(SimpleApplication.class).enqueue(
				new Callable<Void>() {

					@Override
					public Void call() {
						for (Map.Entry<Vector3f, Spatial> entry : nodes
								.entrySet()) {
							entry.getValue().getParent()
									.detachChild(entry.getValue());
						}
						nodes.clear();
						return null;
					}
				});

		vectorList.clear();
	}

	public void updatePath(Vector3f location) {
		if (vectorList.isEmpty()) {
			return;
		}

		if (location.distance(vectorList.get(0)) < 1) {
			vectorList.remove(0);

			updatePath(location);
		}
	}

	public boolean isFinished(Vector3f location) {
		if (vectorList.isEmpty()) {
			clearPath();
			return true;
		}

		if (location.distance(getLast()) < 1) {
			clearPath();
			return true;
		}

		return false;
	}

	public Vector3f getCheckPoint() {
		if (vectorList.isEmpty()) {
			return null;
		}

		return vectorList.get(0);
	}

	public float length() {
		float length = 0;
		for (int i = 0; i < vectorList.size() - 1; ++i) {
			length += vectorList.get(i).distance(vectorList.get(i + 1));
		}

		return length;
	}

}
