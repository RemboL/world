package pl.rembol.jme3.world;

import java.util.ArrayList;
import java.util.List;

import pl.rembol.jme3.world.interfaces.WithDefaultAction;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class RightClickListener implements ActionListener {

	private Camera cam;

	public RightClickListener(Camera cam) {
		this.cam = cam;
	}

	public void onAction(String name, boolean keyPressed, float tpf) {
		if (name.equals("defaultAction") && !keyPressed) {
			List<WithDefaultAction> actors = new ArrayList<>();
			for (Selectable selected : GameState.get().getSelected()) {
				if (selected instanceof WithDefaultAction) {
					actors.add(WithDefaultAction.class.cast(selected));
				}
			}

			if (actors.isEmpty()) {
				System.out.println("no actors to perform default action");
				return;
			}

			Collidable collided = getClosestCollidingObject();

			if (collided != null) {
				if (collided instanceof Node) {
					Selectable selectable = GameState.get().getSelectable(
							Node.class.cast(collided));
					if (selectable != null) {
						for (WithDefaultAction actor : actors) {
							actor.performDefaultAction(selectable);
						}
					}
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
