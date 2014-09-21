package pl.rembol.jme3.world;

import java.util.Random;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

public class BallMan {

	private Node node;
	private AssetManager assetManager;
	private Node leftArmNode;
	private Node rightArmNode;
	private BetterCharacterControl control;

	int phaseCount = 0;

	public BallMan(Vector3f position, Node rootNode, AssetManager assetManager,
			BulletAppState bulletAppState) {
		this.assetManager = assetManager;
		initNode(rootNode);
		node.setLocalTranslation(position);
		node.setLocalRotation(new Quaternion().fromAngleAxis(
				new Random().nextFloat() * FastMath.PI, Vector3f.UNIT_Y));

		control = new BetterCharacterControl(1.5f, 3f, 1);

		bulletAppState.getPhysicsSpace().add(control);

		node.addControl(control);
		control.setViewDirection(new Vector3f(new Random().nextFloat() - .5f,
				0f, new Random().nextFloat() - .5f).normalize());
		control.setWalkDirection(control.getViewDirection().mult(
				new Random().nextFloat() * 5f));

	}

	public void tick() {
		float maxAngle = (1f - (1f / (1f + control.getWalkDirection().length())))
				* FastMath.HALF_PI;

		float phase = (phaseCount++) * FastMath.TWO_PI / 100;

		setArmPivotAngle(FastMath.sin(phase) * maxAngle);
	}

	private void setArmPivotAngle(float angle) {
		leftArmNode.setLocalRotation(new Quaternion().fromAngleAxis(angle,
				Vector3f.UNIT_X));
		rightArmNode.setLocalRotation(new Quaternion().fromAngleAxis(-angle,
				Vector3f.UNIT_X));
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
		leftArmNode.setLocalTranslation(-1.4f, 0f, 0f);
		mainBody.attachChild(leftArmNode);

		rightArmNode = new Node("rightArm");
		rightArmNode.setLocalTranslation(1.4f, 0f, 0f);
		mainBody.attachChild(rightArmNode);

		leftArmNode.attachChild(leftHandGeometry);
		leftHandGeometry.setLocalTranslation(0f, -.6f, 0f);

		rightArmNode.attachChild(rightHandGeometry);
		rightHandGeometry.setLocalTranslation(0f, -.6f, 0f);

	}
}
