package pl.rembol.jme3.world;

import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class LeftClickListener implements ActionListener {

	private Camera cam;

	public LeftClickListener(Camera cam) {
		this.cam = cam;
	}

	public void onAction(String name, boolean keyPressed, float tpf) {
		if (name.equals("select") && !keyPressed) {
			Collidable collided = getClosestCollidingObject();

			if (collided != null) {
				if (collided instanceof Node) {
					Selectable selectable = GameState.get().getSelectable(Node.class
							.cast(collided));
					GameState.get().select(selectable);
				}
			}

		}

	}

	private Collidable getClosestCollidingObject() {
		Ray ray = new Ray(cam.getLocation(), cam.getDirection());

		Float collisionDistance = null;
		Collidable collided = null;

		for (Collidable collidable : GameState.get().getCollidables()) {
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

}
