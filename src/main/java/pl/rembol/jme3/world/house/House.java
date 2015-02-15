package pl.rembol.jme3.world.house;

import java.util.Arrays;
import java.util.List;

import pl.rembol.jme3.player.Player;
import pl.rembol.jme3.player.WithOwner;
import pl.rembol.jme3.world.ModelHelper;
import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.selection.SelectionNode;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class House implements Selectable, Building, WithOwner {

	private static final float SCALE = 5f;
	private RigidBodyControl control;
	private Node house;
	private SelectionNode selectionNode;
	private GameRunningAppState appState;
	private Player owner;

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

		house.setLocalScale(SCALE);
		ModelHelper.setColorToGeometry(house, ColorRGBA.Black, "");

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
			selectionNode.setLocalScale(1/SCALE);
			selectionNode.setLocalTranslation(0, getHeight() / SCALE, 0);
		}
	}

	@Override
	public void deselect() {
		if (selectionNode != null) {
			house.detachChild(selectionNode);
			selectionNode = null;
		}
	}

	@Override
	public List<String> getStatusText() {
		return Arrays.asList("House", "owner: " + owner.getName());
	}

	@Override
	public Player getOwner() {
		return owner;
	}

	@Override
	public void setOwner(Player player) {
		this.owner = player;
		updateColor();
	}

	@Override
	public String[] getGeometriesWithChangeableColor() {
		return new String[] { "hay" };
	}

}
