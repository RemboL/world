package pl.rembol.jme3.world.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

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
import pl.rembol.jme3.world.hud.ActionButton;
import pl.rembol.jme3.world.hud.Clickable;
import pl.rembol.jme3.world.input.state.InputStateManager;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.selection.SelectionIcon;

public class MouseClickListener implements ActionListener, AnalogListener {

    private GameState gameState;

    private boolean isButtonDown = false;

    private Vector2f dragStartPosition;

    private boolean isDragged = false;

    public MouseClickListener(GameState gameState) {
        this.gameState = gameState;

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
                gameState.dragSelectionManager.startDragging();
                gameState.buildingSilhouetteManager.removeSilhouette();
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
            gameState.dragSelectionManager.start();
        }

        if ((name.equals(InputStateManager.LEFT_CLICK) || name
                .equals(InputStateManager.RIGHT_CLICK)) && !keyPressed) {
            if (!isDragged) {
                if (!checkActionButtons(name)) {
                    if (!checkSelectionIcons(name)) {
                        if (name.equals(InputStateManager.LEFT_CLICK) && !checkClickableButtons(name)) {

                            Collidable collided = getClosestCollidingObject();

                            if (collidedWithNode(collided)) {
                                WithNode withNode = gameState.unitRegistry
                                        .getSelectable(Node.class.cast(collided));
                                gameState.inputStateManager.click(name, withNode);
                            } else {
                                Vector3f collisionWithTerrain = getCollisionWithTerrain();
                                if (collisionWithTerrain != null) {
                                    gameState.inputStateManager.click(name, new Vector2f(
                                            collisionWithTerrain.x,
                                            collisionWithTerrain.z));
                                }

                            }
                        }
                    }
                }
                gameState.dragSelectionManager.cancel();
            } else if (name.equals(InputStateManager.LEFT_CLICK)) {
                gameState.dragSelectionManager.confirm();
            }

            isButtonDown = false;
            dragStartPosition = null;
            isDragged = false;
        }

    }
    
    private static Stream<Spatial> getDescendants(Spatial spatial) {
        if (spatial == null) {
            return new ArrayList<Spatial>().stream();
        }
        
        if (!(spatial instanceof Node)) {
            return Collections.singletonList(spatial).stream();
        }
        
        Node node = (Node) spatial;
        return node.getChildren().stream().flatMap(MouseClickListener::getDescendants);
    }

    private boolean checkClickableButtons(String name) {
        Optional<Clickable> clickedButton = getDescendants(gameState.guiNode)
                .filter(Clickable.class::isInstance)
                .map(Clickable.class::cast)
                .filter(clickable -> clickable.isClicked(gameState.inputManager.getCursorPosition()))
                .findFirst();

        if (clickedButton.isPresent()) {
            clickedButton.get().onClick();
            return true;
        }
        return false;

    }

    private boolean checkActionButtons(String name) {
        ActionButton button = getActionButtonClick();
        if (button != null && name.equals(InputStateManager.LEFT_CLICK)) {
            gameState.inputStateManager.type(button.getCommandKey());
            return true;
        }
        return false;
    }

    private boolean checkSelectionIcons(String name) {
        SelectionIcon button = getSelectionIconClick();
        if (button != null && name.equals(InputStateManager.LEFT_CLICK)) {
            gameState.selectionManager.select(button.getSelectable());
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
        for (Spatial button : gameState.actionBox.getActionButtonNode().getChildren()) {

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

        for (Collidable collidable : gameState.unitRegistry.getSelectablesNodes()) {
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
