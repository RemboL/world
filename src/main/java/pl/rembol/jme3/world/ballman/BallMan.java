package pl.rembol.jme3.world.ballman;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.Tree;
import pl.rembol.jme3.world.ballman.action.Action;
import pl.rembol.jme3.world.ballman.action.ChopTreeAction;
import pl.rembol.jme3.world.ballman.action.MoveTowardsTargetAction;
import pl.rembol.jme3.world.ballman.animation.Animation;
import pl.rembol.jme3.world.ballman.animation.WalkAnimation;
import pl.rembol.jme3.world.smallobject.SmallObject;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

public class BallMan {

	private static final float ARM_FLAIL_DELAY_FACTOR = .1f;
	private Node node;
	private AssetManager assetManager;
	private Node leftArmNode;
	private Node rightArmNode;

	private Node leftHandNode;
	private Node rightHandNode;
	private BetterCharacterControl control;

	private Tree selectionTree;

	private float leftArmTargetFlail;
	private float rightArmTargetFlail;
	private Vector3f targetDirection;
	private float targetVelocity = 0;
	private List<Action> actionQueue = new ArrayList<>();

	private Animation animation = new WalkAnimation();

	private SmallObject wielded = null;

	public BallMan(Vector3f position) {
		position.setY(GameState.getTerrain().getTerrain()
				.getHeight(new Vector2f(position.x, position.z))
				+ GameState.getTerrain().getTerrain().getLocalTranslation().y
				+ 2f);

		this.assetManager = GameState.getAssetManager();
		initNode(GameState.getRootNode());
		node.setLocalTranslation(position);
		node.setLocalRotation(new Quaternion().fromAngleAxis(
				new Random().nextFloat() * FastMath.PI, Vector3f.UNIT_Y));

		control = new BetterCharacterControl(1.5f, 3f, 1);

		GameState.getBulletAppState().getPhysicsSpace().add(control);

		node.addControl(control);
		control.setViewDirection(new Vector3f(new Random().nextFloat() - .5f,
				0f, new Random().nextFloat() - .5f).normalize());
		control.setWalkDirection(control.getViewDirection().mult(
				new Random().nextFloat() * 5f));

	}

	public void setLeftArmFlail(float flail, boolean immediate) {
		leftArmTargetFlail = flail;
		if (immediate) {
			leftArmNode.setLocalRotation(new Quaternion().fromAngleAxis(
					leftArmTargetFlail, Vector3f.UNIT_X));
		}
	}

	public void setRightArmFlail(float flail, boolean immediate) {
		rightArmTargetFlail = flail;
		if (immediate) {
			rightArmNode.setLocalRotation(new Quaternion().fromAngleAxis(
					rightArmTargetFlail, Vector3f.UNIT_X));
		}

	}

	public float getWalkSpeed() {
		return control.getWalkDirection().length();
	}

	public void tick() {

		if (!actionQueue.isEmpty()) {
			actionQueue.get(0).act(this);

			if (actionQueue.get(0).isFinished(this)) {
				actionQueue.remove(0);
			}
		}

		if (animation != null) {
			animation.tick(this);
		}

		leftArmNode.setLocalRotation(leftArmNode
				.getLocalRotation()
				.add(new Quaternion().fromAngleAxis(leftArmTargetFlail,
						Vector3f.UNIT_X).mult(ARM_FLAIL_DELAY_FACTOR))
				.mult(1 / (1 + ARM_FLAIL_DELAY_FACTOR)));
		rightArmNode.setLocalRotation(rightArmNode
				.getLocalRotation()
				.add(new Quaternion().fromAngleAxis(rightArmTargetFlail,
						Vector3f.UNIT_X).mult(ARM_FLAIL_DELAY_FACTOR))
				.mult(1 / (1 + ARM_FLAIL_DELAY_FACTOR)));
		if (targetDirection != null) {
			control.setViewDirection(control.getViewDirection()
					.add(targetDirection).setY(0).normalize());
		}
		control.setWalkDirection(control.getViewDirection().mult(
				(targetVelocity + getWalkSpeed()) / 2));

	}

	private void initNode(Node rootNode) {
		node = new Node("ballMan");
		rootNode.attachChild(node);

		Node mainBody = new Node("mainBody");
		node.attachChild(mainBody);
		mainBody.setLocalTranslation(0f, 1f, 0f);

		Sphere mainBallSphere = new Sphere(32, 32, 1f);
		Sphere eyeBallSphere = new Sphere(6, 6, .15f);
		Sphere handBallSphere = new Sphere(12, 12, .4f);

		Material skinMaterial = new Material(assetManager,
				"Common/MatDefs/Light/Lighting.j3md");
		skinMaterial.setBoolean("UseMaterialColors", true);
		skinMaterial.setColor("Diffuse", ColorRGBA.Yellow);
		skinMaterial.setColor("Specular", ColorRGBA.White);
		skinMaterial.setColor("Ambient", ColorRGBA.Yellow.mult(.1f));
		skinMaterial.setFloat("Shininess", 32f);

		Material eyeMaterial = new Material(assetManager,
				"Common/MatDefs/Light/Lighting.j3md");
		eyeMaterial.setBoolean("UseMaterialColors", true);
		eyeMaterial.setColor("Diffuse", ColorRGBA.Black);
		eyeMaterial.setColor("Specular", ColorRGBA.White);
		eyeMaterial.setFloat("Shininess", 96f);

		Geometry mainBallGeometry = new Geometry("mainBall", mainBallSphere);
		mainBallGeometry.setMaterial(skinMaterial);
		mainBallGeometry.setShadowMode(ShadowMode.CastAndReceive);

		Geometry leftEyeBallGeometry = new Geometry("leftEyeBall",
				eyeBallSphere);
		leftEyeBallGeometry.setMaterial(eyeMaterial);
		leftEyeBallGeometry.setShadowMode(ShadowMode.Off);

		Geometry rightEyeBallGeometry = new Geometry("rightEyeBall",
				eyeBallSphere);
		rightEyeBallGeometry.setMaterial(eyeMaterial);
		rightEyeBallGeometry.setShadowMode(ShadowMode.Off);

		Geometry leftHandGeometry = new Geometry("leftHand", handBallSphere);
		leftHandGeometry.setMaterial(skinMaterial);
		leftHandGeometry.setShadowMode(ShadowMode.CastAndReceive);

		Geometry rightHandGeometry = new Geometry("rightHand", handBallSphere);
		rightHandGeometry.setMaterial(skinMaterial);
		rightHandGeometry.setShadowMode(ShadowMode.CastAndReceive);

		mainBody.attachChild(mainBallGeometry);

		leftEyeBallGeometry.setLocalTranslation(-.35f, .6f, .7f);
		mainBody.attachChild(leftEyeBallGeometry);

		rightEyeBallGeometry.setLocalTranslation(.35f, .6f, .7f);
		mainBody.attachChild(rightEyeBallGeometry);

		leftArmNode = new Node("leftArm");
		leftArmNode.setLocalTranslation(1.4f, 0f, 0f);
		mainBody.attachChild(leftArmNode);

		rightArmNode = new Node("rightArm");
		rightArmNode.setLocalTranslation(-1.4f, 0f, 0f);
		mainBody.attachChild(rightArmNode);

		leftHandNode = new Node("leftHand");
		leftHandNode.setLocalTranslation(0f, -.6f, 0f);
		leftArmNode.attachChild(leftHandNode);

		leftHandNode.attachChild(leftHandGeometry);

		rightHandNode = new Node("rightHand");
		rightHandNode.setLocalTranslation(0f, -.6f, 0f);
		rightHandNode.setLocalRotation(new Quaternion().fromAngleAxis(
				FastMath.QUARTER_PI, Vector3f.UNIT_X));
		rightArmNode.attachChild(rightHandNode);

		rightHandNode.attachChild(rightHandGeometry);

	}

	public void wield(SmallObject item) {
		if (wielded != null) {
			drop();
		}

		item.attach(rightHandNode);
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

	public void lookTorwards(Tree target) {
		targetDirection = target.getLocation()
				.subtract(node.getWorldTranslation()).setY(0).normalize();
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

	public void setAnimation(Animation animation) {
		if (animation == null) {
			this.animation = new WalkAnimation();
		} else {
			this.animation = animation;
		}
	}
}
