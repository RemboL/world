package pl.rembol.jme3.world.warehouse;

import java.util.Arrays;
import java.util.List;

import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.selection.SelectionNode;
import pl.rembol.jme3.world.smallobject.Log;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Warehouse implements Selectable, Building {

	private static final float SCALE = 5f;
	private RigidBodyControl control;
	private Node house;
	private SelectionNode selectionNode;
	private GameRunningAppState appState;
	private int resources = 0;

	public Warehouse(Vector2f position, GameRunningAppState appState) {
		this(position, appState, false);
	}

	public Warehouse(Vector2f position, GameRunningAppState appState,
			boolean startUnderGround) {
		this(appState.getTerrain().getGroundPosition(position), appState,
				startUnderGround);
	}

	public Warehouse(Vector3f position, GameRunningAppState appState,
			boolean startUnderGround) {
		this.appState = appState;

		house = (Node) appState.getAssetManager().loadModel(
				"warehouse/warehouse.scene");

		house.setShadowMode(ShadowMode.CastAndReceive);
		appState.getRootNode().attachChild(house);

		if (startUnderGround) {
			position.subtract(Vector3f.UNIT_Y.mult(getHeight()));
		}

		house.setLocalTranslation(position);

		house.setLocalScale(SCALE);

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
			selectionNode.setLocalScale(1 / SCALE);
		}
	}

	@Override
	public void deselect() {
		if (selectionNode != null) {
			house.detachChild(selectionNode);
			selectionNode = null;
		}
	}

	public void increaseResources(int resources) {
		this.resources += resources;

		appState.getSelectionManager().updateSelectionText();

		System.out.println("resources increased to " + this.resources);
	}

	public void returnResource(BallMan ballMan) {
		if (ballMan.getWieldedObject() instanceof Log) {
			Log log = (Log) ballMan.getWieldedObject();

			increaseResources(log.getResourceCount());
			ballMan.dropAndDestroy();
		}

	}

	@Override
	public List<String> getStatusText() {
		return Arrays.asList("Warehouse", "Logs: " + resources);
	}

}
