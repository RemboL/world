package pl.rembol.jme3.world.input;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.hud.ActionBox;
import pl.rembol.jme3.world.hud.ActionButton;
import pl.rembol.jme3.world.input.state.BuildingSilhouetteManager;
import pl.rembol.jme3.world.input.state.InputStateManager;
import pl.rembol.jme3.world.input.state.SelectionManager;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.selection.SelectionIcon;

@Component
public class MouseClickListener implements ActionListener, AnalogListener {

	@Autowired
	private InputStateManager inputStateManager;

	@Autowired
	private ActionBox actionBox;

	@Autowired
	private SelectionManager selectionManager;

	@Autowired
	private GameState gameState;

	@Autowired
	private DragSelectionManager dragSelectionManager;

	@Autowired
	private BuildingSilhouetteManager buildingSilhouetteManager;

	@Autowired
	private UnitRegistry unitRegistry;

	private boolean isButtonDown = false;

	private Vector2f dragStartPosition;

	private boolean isDragged = false;

	@PostConstruct
	public void registerInput() {
		gameState.inputManager.addMapping(InputStateManager.LEFT_CLICK,
				new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		gameState.inputManager.addListener(this, InputStateManager.LEFT_CLICK);

		gameState.inputManager.addMapping(InputStateManager.RIGHT_CLICK,
				new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		gameState.inputManager.addListener(this, InputStateManager.RIGHT_CLICK);

		gameState.inputManager.addMapping(InputStateManager.MOUSE_MOVE, 
				new MouseAxisTrigger(MouseInput.AXIS_X, false),
				new MouseAxisTrigger(MouseInput.AXIS_X, true),
				new MouseAxisTrigger(MouseInput.AXIS_Y, false),
				new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		gameState.inputManager.addListener(this, InputStateManager.MOUSE_MOVE);
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		if (isButtonDown
				&& dragStartPosition != null
				&& dragStartPosition.distance(gameState.inputManager.getCursorPosition()) > 5f) {
			if (!isDragged) {
				dragSelectionManager.startDragging();
				buildingSilhouetteManager.removeSilhouette();
			}
			isDragged = true;
		}
	}

	@Override
	public void onAction(String name, boolean keyPressed, float tpf) {

		if (name.equals(InputStateManager.LEFT_CLICK) && keyPressed) {
			isButtonDown = true;
			dragStartPosition = gameState.inputManager.getCursorPosition().clone();
			isDragged = false;
			dragSelectionManager.start();
		}

		if ((name.equals(InputStateManager.LEFT_CLICK) || name
				.equals(InputStateManager.RIGHT_CLICK)) && !keyPressed) {
			if (!isDragged) {
				if (!checkActionButtons(name)) {
					if (!checkSelectionIcons(name)) {

						Collidable collided = getClosestCollidingObject();

						if (collidedWithNode(collided)) {
							WithNode withNode = unitRegistry
									.getSelectable(Node.class.cast(collided));
							inputStateManager.click(name, withNode);
						} else {
							Vector3f collisionWithTerrain = getCollisionWithTerrain();
							if (collisionWithTerrain != null) {
								inputStateManager.click(name, new Vector2f(
										collisionWithTerrain.x,
										collisionWithTerrain.z));
							}

						}
					}
				}
				dragSelectionManager.cancel();
			} else if (name.equals(InputStateManager.LEFT_CLICK)) {
				dragSelectionManager.confirm();
			}

			isButtonDown = false;
			dragStartPosition = null;
			isDragged = false;
		}

	}

	private boolean checkActionButtons(String name) {
		ActionButton button = getActionButtonClick();
		if (button != null && name.equals(InputStateManager.LEFT_CLICK)) {
			inputStateManager.type(button.getCommandKey());
			return true;
		}
		return false;
	}

	private boolean checkSelectionIcons(String name) {
		SelectionIcon button = getSelectionIconClick();
		if (button != null && name.equals(InputStateManager.LEFT_CLICK)) {
			selectionManager.select(button.getSelectable());
			return true;
		}
		return false;
	}

	private SelectionIcon getSelectionIconClick() {
		for (SelectionIcon button : gameState.statusBar.getSelectionIcons()) {

			Vector2f click2d = gameState.inputManager.getCursorPosition();

			Vector2f buttonStart = new Vector2f(button.getWorldTranslation().x,
					button.getWorldTranslation().y);
			Vector2f buttonEnd = buttonStart.add(new Vector2f(
					ActionButton.SIZE, ActionButton.SIZE));

			if (buttonStart.x <= click2d.x && buttonStart.y <= click2d.y
					&& buttonEnd.x > click2d.x && buttonEnd.y > click2d.y) {
				return button;
			}
		}

		return null;
	}

	private ActionButton getActionButtonClick() {
		for (Spatial button : actionBox.getActionButtonNode().getChildren()) {

			Vector2f click2d = gameState.inputManager.getCursorPosition();

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

		for (Collidable collidable : unitRegistry.getSelectablesNodes()) {
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
		Vector2f click2d = gameState.inputManager.getCursorPosition();

		Vector3f click3d = gameState.camera.getWorldCoordinates(
				new Vector2f(click2d.getX(), click2d.getY()), 0f);

		Vector3f dir = gameState.camera
				.getWorldCoordinates(
						new Vector2f(click2d.getX(), click2d.getY()), 1f)
				.subtractLocal(click3d).normalize();

		return new Ray(click3d, dir);
	}

	private Vector3f getCollisionWithTerrain() {
		Ray ray = getClickRay();

		CollisionResults results = new CollisionResults();
		gameState.terrain.getTerrain().collideWith(ray, results);

		CollisionResult collision = results.getClosestCollision();

		if (collision != null) {
			return collision.getContactPoint();
		}

		return null;
	}

}
