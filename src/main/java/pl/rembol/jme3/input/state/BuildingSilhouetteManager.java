package pl.rembol.jme3.input.state;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.ballman.order.BuildOrder;
import pl.rembol.jme3.world.ballman.order.Order;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.terrain.Terrain;

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
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

@Component
public class BuildingSilhouetteManager extends AbstractControl implements
		ApplicationContextAware {

	private Node silhouette;

	private Material greenMaterial;

	private Material redMaterial;

	@Autowired
	private AssetManager assetManager;

	@Autowired
	private Terrain terrain;

	@Autowired
	private InputManager inputManager;

	@Autowired
	private Camera camera;

	@Autowired
	private Node rootNode;

	@Autowired
	private UnitRegistry gameState;

	private ApplicationContext applicationContext;

	private Building building;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@PostConstruct
	public void initMaterials() {
		greenMaterial = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		greenMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		greenMaterial.setColor("Color", new ColorRGBA(.5f, 1f, .5f, .2f));

		redMaterial = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		redMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		redMaterial.setColor("Color", new ColorRGBA(1f, .5f, .5f, .2f));
	}

	public void createSilhouette(Order<?> currentOrder) {
		if (BuildOrder.class.isInstance(currentOrder)) {
			Vector3f newPosition = getNewPosition();

			if (newPosition != null) {
				building = createBuilding(currentOrder);
				silhouette = building.initNode();
				silhouette.setMaterial(greenMaterial);
				silhouette.setLocalTranslation(newPosition);
				silhouette.addControl(this);
				silhouette.setQueueBucket(Bucket.Transparent);
				rootNode.attachChild(silhouette);
			}
		}
	}

	private Building createBuilding(Order<?> currentOrder) {
		return applicationContext
				.getAutowireCapableBeanFactory()
				.createBean(
						BuildOrder.class.cast(currentOrder).getActionClass())
				.createBuilding();
	}

	public Vector3f getSilhouettePosition() {
		if (silhouette != null) {
			return silhouette.getLocalTranslation();
		}

		return null;
	}

	public void removeSilhouette() {
		if (silhouette != null) {
			if (silhouette.getControl(BuildingSilhouetteManager.class) != null) {
				silhouette.removeControl(this);
			}

			rootNode.detachChild(silhouette);
			silhouette = null;
		}
		building = null;
	}

	@Override
	protected void controlRender(RenderManager arg0, ViewPort arg1) {
	}

	@Override
	protected void controlUpdate(float tpf) {
		Vector3f newPosition = getNewPosition();
		if (newPosition != null) {
			silhouette.setLocalTranslation(newPosition);
			if (gameState.isSpaceFreeWithBuffer(newPosition,
					building.getWidth())) {
				silhouette.setMaterial(greenMaterial);
			} else {
				silhouette.setMaterial(redMaterial);
			}
		}

	}

	private Vector3f getNewPosition() {
		Vector3f clickPosition = getCollisionWithTerrain();
		if (clickPosition != null) {
			Vector2f roundedPosition2d = new Vector2f(
					Math.round(clickPosition.x), Math.round(clickPosition.z));
			return terrain.getGroundPosition(roundedPosition2d);
		}

		return null;
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

}
