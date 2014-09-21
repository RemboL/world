package pl.rembol.jme3.world;

import java.util.Random;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Tree {

	private BetterCharacterControl control;

	public Tree(Vector3f position, Terrain terrain, Node rootNode,
			AssetManager assetManager, BulletAppState bulletAppState) {

		position.setY(terrain.getTerrain().getHeight(
				new Vector2f(position.x, position.z))
				+ terrain.getTerrain().getLocalTranslation().y);
		Node tree = (Node) assetManager.loadModel("tree.blend");
		tree.setShadowMode(ShadowMode.Cast);
		rootNode.attachChild(tree);
		tree.setLocalTranslation(position);

		control = new BetterCharacterControl(1.5f, 5f, 0);
		tree.addControl(control);

		control.setViewDirection(new Vector3f(new Random().nextFloat() - .5f,
				0f, new Random().nextFloat() - .5f).normalize());

		bulletAppState.getPhysicsSpace().add(control);

	}

}
