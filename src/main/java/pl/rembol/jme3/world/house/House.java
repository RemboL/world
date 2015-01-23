package pl.rembol.jme3.world.house;

import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.selection.SelectionNode;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class House implements Selectable, Building {

	private RigidBodyControl control;
	private Node house;
	private SelectionNode selectionNode;
	private GameRunningAppState appState;

	public House(Vector2f position, GameRunningAppState appState) {
		this(position, appState, false);
	}

	public House(Vector2f position, GameRunningAppState appState,
			boolean startUnderGround) {
		this(appState.getTerrain().getGroundPosition(position), appState,
				startUnderGround);
	}

	public House(Vector3f position, GameRunningAppState appState,
			boolean startUnderGround) {
		this.appState = appState;

		house = (Node) appState.getAssetManager().loadModel(
				"house2/house2.scene");

		house.setShadowMode(ShadowMode.Cast);
		appState.getRootNode().attachChild(house);

		if (startUnderGround) {
			position.subtract(Vector3f.UNIT_Y.mult(getHeight()));
		}

		house.setLocalTranslation(position);

		house.setLocalScale(5f);

		control = new RigidBodyControl(0f);
		house.addControl(control);

		appState.getBulletAppState().getPhysicsSpace().add(control);

		GameState.get().register(this);
	}

	@Override
	public float getHeight() {
		return 15f;
	}

	@Override
	public float getWidth() {
		return 5f;
	}

	public Vector3f getLocation() {
		return house.getWorldTranslation();
	}

	@Override
	public Node getNode() {
		return house;
	}

	@Override
	public void select() {
		if (selectionNode == null) {
			selectionNode = new SelectionNode(appState.getAssetManager());
			house.attachChild(selectionNode);
			selectionNode.setLocalTranslation(0, 10, 0);
		}
	}

	@Override
	public void deselect() {
		if (selectionNode != null) {
			house.detachChild(selectionNode);
			selectionNode = null;
		}
	}

}
