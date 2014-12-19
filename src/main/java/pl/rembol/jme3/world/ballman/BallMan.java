package pl.rembol.jme3.world.ballman;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.Tree;
import pl.rembol.jme3.world.ballman.action.Action;
import pl.rembol.jme3.world.ballman.action.ChopTreeAction;
import pl.rembol.jme3.world.ballman.action.MoveTowardsTargetAction;
import pl.rembol.jme3.world.interfaces.WithDefaultAction;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.selection.SelectionNode;
import pl.rembol.jme3.world.smallobject.SmallObject;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

public class BallMan extends AbstractControl implements Selectable, WithDefaultAction {

	private Node node;
	private Node selectionNode;
	private BetterCharacterControl control;

	private Tree selectionTree;

	private Vector3f targetDirection;
	private float targetVelocity = 0;
	private List<Action> actionQueue = new ArrayList<>();

	private SmallObject wielded = null;
	private AnimControl animationControl;
	private AnimChannel animationChannel;
	private GameRunningAppState appState;

	public BallMan(Vector2f position, GameRunningAppState appState) {
		this(new Vector3f(position.x, appState.getTerrainQuad()
				.getHeight(new Vector2f(position.x, position.y))
				+ appState.getTerrainQuad().getLocalTranslation().y,
				position.y), appState);
	}

	public BallMan(Vector3f position, GameRunningAppState appState) {
		this.appState = appState;
		initNode(appState.getRootNode());
		node.setLocalTranslation(position);
		node.setLocalRotation(new Quaternion().fromAngleAxis(
				new Random().nextFloat() * FastMath.PI, Vector3f.UNIT_Y));

		control = new BetterCharacterControl(1.5f, 3f, 1);
		
		node.addControl(this);
		
		appState.getBulletAppState().getPhysicsSpace().add(control);

		node.addControl(control);
		control.setViewDirection(new Vector3f(new Random().nextFloat() - .5f,
				0f, new Random().nextFloat() - .5f).normalize());
		control.setWalkDirection(control.getViewDirection().mult(
				new Random().nextFloat() * 5f));

		GameState.get().register(this);

	}

	@Override
	public Node getNode() {
		return node;
	}

	public float getWalkSpeed() {
		return control.getWalkDirection().length();
	}

	private void initNode(Node rootNode) {
		node = (Node) appState.getAssetManager().loadModel("ballman/ballman.mesh.xml");

		rootNode.attachChild(node);
		node.setShadowMode(ShadowMode.Cast);
		animationControl = node.getControl(AnimControl.class);
		animationChannel = animationControl.createChannel();
		animationChannel.setAnim("stand");
	}

	public void wield(SmallObject item) {
		if (wielded != null) {
			drop();
		}

		item.attach(node.getControl(SkeletonControl.class).getAttachmentsNode(
				"hand.R"));

		wielded = item;

	}

	public void drop() {
		if (wielded != null) {
			wielded.detach();
			wielded = null;
		}
	}

	public void attack(Tree tree) {
		this.selectionTree = tree;
		addAction(new MoveTowardsTargetAction(this.selectionTree, 5));
		addAction(new ChopTreeAction(this.selectionTree));
	}

	public void lookTowards(WithNode target) {
		lookTowards(target.getNode().getWorldTranslation());
	}

	public void lookTowards(Vector3f location) {
		targetDirection = location.subtract(node.getWorldTranslation()).setY(0)
				.normalize();
	}

	public void setTargetVelocity(float targetVelocity) {
		this.targetVelocity = targetVelocity;
	}

	public Vector3f getLocation() {
		return node.getWorldTranslation();
	}

	public void addAction(Action action) {
		actionQueue.add(action);
	}

	public void setAnimation(String animationName, LoopMode loopMode) {
		animationChannel.setAnim(animationName);
		animationChannel.setLoopMode(loopMode);
	}

	@Override
	public void select() {
		if (selectionNode == null) {
			selectionNode = new SelectionNode(appState.getAssetManager());
			node.attachChild(selectionNode);
			selectionNode.setLocalTranslation(0, 5, 0);
		}
	}

	@Override
	public void deselect() {
		if (selectionNode != null) {
			node.detachChild(selectionNode);
			selectionNode = null;
		}
	}

	@Override
	public void performDefaultAction(Selectable target) {
		actionQueue.clear();
		if (target instanceof Tree) {
			attack(Tree.class.cast(target));
		} else {
			addAction(new MoveTowardsTargetAction(target, 5f));
		}
	}

	@Override
	protected void controlUpdate(float paramFloat) {
		if (!actionQueue.isEmpty()) {
			actionQueue.get(0).act(this, appState);

			if (actionQueue.get(0).isFinished(this)) {
				actionQueue.get(0).finish();
				actionQueue.remove(0);
				animationChannel.setAnim("stand");
			}
		}

		if (targetDirection != null) {
			control.setViewDirection(control.getViewDirection()
					.add(targetDirection).setY(0).normalize());
		}

		control.setWalkDirection(control.getViewDirection().mult(
				(targetVelocity + getWalkSpeed()) / 2));
	}

	@Override
	protected void controlRender(RenderManager paramRenderManager,
			ViewPort paramViewPort) {
	}

}
