package pl.rembol.jme3.world;

import java.util.Random;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.smallobject.Log;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Tree {

	private BetterCharacterControl control;
	private Node tree;

	public Tree(Vector3f position) {

		position.setY(GameState.getTerrain().getTerrain()
				.getHeight(new Vector2f(position.x, position.z))
				+ GameState.getTerrain().getTerrain().getLocalTranslation().y);
		tree = (Node) GameState.getAssetManager().loadModel("tree.blend");
		tree.setShadowMode(ShadowMode.Cast);
		GameState.getRootNode().attachChild(tree);
		tree.setLocalTranslation(position);

		control = new BetterCharacterControl(1.5f, 5f, 0);
		tree.addControl(control);

		control.setViewDirection(new Vector3f(new Random().nextFloat() - .5f,
				0f, new Random().nextFloat() - .5f).normalize());

		GameState.getBulletAppState().getPhysicsSpace().add(control);

	}

	public Vector3f getLocation() {
		return tree.getWorldTranslation();
	}

	public void getChoppedBy(BallMan ballMan) {
		new Log(tree.getWorldTranslation().add(ballMan.getLocation()).mult(.5f)
				.add(Vector3f.UNIT_Y.mult(2f)));
	}

}
