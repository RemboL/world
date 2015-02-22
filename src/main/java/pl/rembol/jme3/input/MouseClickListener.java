package pl.rembol.jme3.input;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.rembol.jme3.input.state.InputStateManager;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.hud.ActionBox;
import pl.rembol.jme3.world.hud.ActionButton;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

@Component
public class MouseClickListener implements ActionListener {

	public static final String LEFT_CLICK = "input_leftClick";

	public static final String RIGHT_CLICK = "input_rightClick";

	@Autowired
	private InputManager inputManager;

	@Autowired
	private InputStateManager inputStateManager;

	@Autowired
	private ActionBox actionBox;

	@Autowired
	private Camera camera;

	@Autowired
	private Terrain terrain;

	@PostConstruct
	public void registerInput() {
		inputManager.addMapping(InputStateManager.LEFT_CLICK,
				new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addListener(this, InputStateManager.LEFT_CLICK);

		inputManager.addMapping(InputStateManager.RIGHT_CLICK,
				new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addListener(this, InputStateManager.RIGHT_CLICK);
	}

	public void onAction(String name, boolean keyPressed, float tpf) {

		if ((name.equals(InputStateManager.LEFT_CLICK) || name
				.equals(InputStateManager.RIGHT_CLICK)) && !keyPressed) {

			ActionButton button = getActionButtonClick();
			if (button != null) {
				inputStateManager.type(button.getCommandKey());
				return;
			}

			Collidable collided = getClosestCollidingObject();

			if (collidedWithNode(collided)) {
				Selectable selectable = GameState.get().getSelectable(
						Node.class.cast(collided));
				inputStateManager.click(name, selectable);
			} else {
				Vector3f collisionWithTerrain = getCollisionWithTerrain();
				if (collisionWithTerrain != null) {
					inputStateManager.click(name, new Vector2f(
							collisionWithTerrain.x, collisionWithTerrain.z));
				}

			}
		}

	}

	private ActionButton getActionButtonClick() {
		for (Spatial button : actionBox.getActionButtonNode().getChildren()) {

			Vector2f click2d = inputManager.getCursorPosition();

			Vector2f buttonStart = new Vector2f(button.getWorldTranslation().x,
					button.getWorldTranslation().y);
			Vector2f buttonEnd = buttonStart.add(new Vector2f(
					ActionButton.SIZE, ActionButton.SIZE));

			if (buttonStart.x <= click2d.x && buttonStart.y <= click2d.y
					&& buttonEnd.x > click2d.x && buttonEnd.y > click2d.y) {
				return ActionButton.class.cast(button);
			}
		}

		return null;
	}

	private boolean collidedWithNode(Collidable collided) {
		return collided != null && collided instanceof Node;
	}

	private Collidable getClosestCollidingObject() {
		Ray ray = getClickRay();

		Float collisionDistance = null;
		Collidable collided = null;

		for (Collidable collidable : GameState.get().getSelectablesNodes()) {
			CollisionResults results = new CollisionResults();
			collidable.collideWith(ray, results);

			CollisionResult collision = results.getClosestCollision();

			if (collision != null) {
				if (collisionDistance == null
						|| collision.getDistance() < collisionDistance) {
					collisionDistance = collision.getDistance();
					collided = collidable;
				}
			}
		}
		return collided;
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
