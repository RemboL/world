package pl.rembol.jme3.world.pathfinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

public class VectorPath {

	private static Material greenMaterial = null;
	private static Material redMaterial = null;

	private List<Vector3f> vectorList;

	private Map<Vector3f, Spatial> nodes = new HashMap<>();

	public VectorPath(Vector2iPath path, ApplicationContext applicationContext,
			Set<Vector2i> nodesVisited) {
		initMaterials(applicationContext.getBean(AssetManager.class));

		vectorList = path.vectorList
				.stream()
				.map(vector2i -> applicationContext.getBean(Terrain.class)
						.getGroundPosition(vector2i.asVector2f()))
				.collect(Collectors.toList());

		for (Vector3f vector : vectorList) {
			Geometry node = new Geometry("path node", new Sphere(5, 5, .5f));
			node.setMaterial(greenMaterial);
			node.setLocalTranslation(vector);
			applicationContext.getBean("rootNode", Node.class)
					.attachChild(node);
			nodes.put(vector, node);
		}

		for (Vector2i visited : nodesVisited) {
			if (!path.vectorList.contains(visited)) {
				Geometry node = new Geometry("path node", new Sphere(5, 5, .5f));
				node.setMaterial(redMaterial);
				node.setLocalTranslation(applicationContext.getBean(
						Terrain.class).getGroundPosition(visited.asVector2f()));
				applicationContext.getBean("rootNode", Node.class).attachChild(
						node);
				nodes.put(applicationContext.getBean(Terrain.class)
						.getGroundPosition(visited.asVector2f()), node);
			}
		}
	}

	private static void initMaterials(AssetManager assetManager) {
		if (greenMaterial == null) {
			greenMaterial = new Material(assetManager,
					"Common/MatDefs/Misc/Unshaded.j3md");
			greenMaterial.getAdditionalRenderState().setBlendMode(
					BlendMode.Alpha);
			greenMaterial.setColor("Color", new ColorRGBA(.5f, 1f, .5f, .2f));
		}

		if (redMaterial == null) {
			redMaterial = new Material(assetManager,
					"Common/MatDefs/Misc/Unshaded.j3md");
			redMaterial.getAdditionalRenderState()
					.setBlendMode(BlendMode.Alpha);
			redMaterial.setColor("Color", new ColorRGBA(1f, .5f, .5f, .2f));
		}
	}

	public Vector3f getLast() {
		if (vectorList.isEmpty()) {
			return null;
		}

		return vectorList.get(vectorList.size() - 1);
	}

	public void clearPath() {
		for (Map.Entry<Vector3f, Spatial> entry : nodes.entrySet()) {
			entry.getValue().getParent().detachChild(entry.getValue());
		}
		
		nodes.clear();
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
			return true;
		}

		return location.distance(getLast()) < 1;
	}

	public Vector3f getCheckPoint() {
		if (vectorList.isEmpty()) {
			return null;
		}

		return vectorList.get(0);
	}

}
