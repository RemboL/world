package pl.rembol.jme3.world.warehouse;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import pl.rembol.jme3.player.Player;
import pl.rembol.jme3.player.WithOwner;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.selection.SelectionNode;
import pl.rembol.jme3.world.smallobject.Log;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Warehouse implements Selectable, Building, WithOwner {

	private RigidBodyControl control;
	private Node house;
	private SelectionNode selectionNode;
	private Player owner;

	@Autowired
	private Node rootNode;

	@Autowired
	private BulletAppState bulletAppState;

	@Autowired
	private AssetManager assetManager;

	@Autowired
	private Terrain terrain;

	public Warehouse init(Vector2f position) {
		return init(position, false);
	}

	public Warehouse init(Vector2f position, boolean startUnderGround) {
		return init(terrain.getGroundPosition(position), startUnderGround);
	}

	public Warehouse init(Vector3f position, boolean startUnderGround) {

		house = (Node) assetManager.loadModel("warehouse/warehouse.scene");

		house.setShadowMode(ShadowMode.CastAndReceive);
		rootNode.attachChild(house);

		if (startUnderGround) {
			position.subtract(Vector3f.UNIT_Y.mult(getHeight()));
		}

		house.setLocalTranslation(position);

		house.setLocalScale(getWidth());

		control = new RigidBodyControl(0f);
		house.addControl(control);

		bulletAppState.getPhysicsSpace().add(control);

		GameState.get().register(this);

		return this;
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
			selectionNode = new SelectionNode(assetManager);
			house.attachChild(selectionNode);
			selectionNode.setLocalTranslation(0, .1f, 0);
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
		if (owner != null) {
			owner.addWood(resources);
		}
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
		return Arrays.asList("Warehouse");
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
		return new String[] { "Flag" };
	}

	@Override
	public String getIconName() {
		return "warehouse";
	}

}
