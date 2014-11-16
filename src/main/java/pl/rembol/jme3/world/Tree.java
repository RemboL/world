package pl.rembol.jme3.world;

import java.util.Random;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.selection.SelectionNode;
import pl.rembol.jme3.world.smallobject.Log;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Tree implements Selectable {

	private BetterCharacterControl control;
	private Node tree;
	private int hp = 1000;
	private int maxHp = 1000;
	private boolean destroyed = false;
	private SelectionNode selectionNode;

	public Tree(Vector2f position) {
		this(new Vector3f(position.x, GameState.get().getTerrain()
				.getHeight(new Vector2f(position.x, position.y))
				+ GameState.get().getTerrain().getLocalTranslation().y,
				position.y));
	}

	public Tree(Vector3f position) {

		tree = BlenderLoaderHelper.rewriteDiffuseToAmbient((Node) GameState
				.get().getAssetManager().loadModel("tree.blend"));
		tree.setShadowMode(ShadowMode.Cast);
		GameState.get().getRootNode().attachChild(tree);
		tree.setLocalTranslation(position);

		control = new BetterCharacterControl(1.5f, 5f, 0);
		tree.addControl(control);

		control.setViewDirection(new Vector3f(new Random().nextFloat() - .5f,
				0f, new Random().nextFloat() - .5f).normalize());

		GameState.get().getBulletAppState().getPhysicsSpace().add(control);

		GameState.get().register(this);
	}

	protected void setHp(int hp) {
		this.hp = hp;

		tree.setLocalScale((1f * this.hp + this.maxHp) / (2 * this.maxHp));

		if (hp < 0) {
			destroy();
		}
	}

	protected void substractHp(int hp) {
		setHp(this.hp - hp);
	}

	private void destroy() {
		System.out.println("TIMBEEER!!!");
		GameState.get().unregister(this);
		GameState.get().getBulletAppState().getPhysicsSpace().remove(control);
		tree.getParent().detachChild(tree);
		this.destroyed = true;
	}

	public boolean isDestroyed() {
		return this.destroyed;
	}

	public Vector3f getLocation() {
		return tree.getWorldTranslation();
	}

	public void getChoppedBy(BallMan ballMan) {
		new Log(tree.getWorldTranslation().add(ballMan.getLocation()).mult(.5f)
				.add(Vector3f.UNIT_Y.mult(2f)));

		substractHp(FastMath.nextRandomInt(50, 100));
	}

	@Override
	public Node getNode() {
		return tree;
	}

	@Override
	public void select() {
		if (selectionNode == null) {
			selectionNode = new SelectionNode(GameState.get().getAssetManager());
			tree.attachChild(selectionNode);
			selectionNode.setLocalTranslation(0, 10, 0);
		}
	}

	@Override
	public void deselect() {
		if (selectionNode != null) {
			tree.detachChild(selectionNode);
			selectionNode = null;
		}
	}

}
