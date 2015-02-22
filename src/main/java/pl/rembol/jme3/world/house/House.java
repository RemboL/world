package pl.rembol.jme3.world.house;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.player.Player;
import pl.rembol.jme3.player.WithOwner;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ModelHelper;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.selection.SelectionNode;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class House implements Selectable, Building, WithOwner {

	private RigidBodyControl control;
	private Node house;
	private SelectionNode selectionNode;
	private Player owner;
	private ApplicationContext applicationContext;

	public House(ApplicationContext applicationContext, Vector2f position) {
		this(applicationContext, position, false);
	}

	public House(ApplicationContext applicationContext, Vector2f position,
			boolean startUnderGround) {
		this(applicationContext, applicationContext.getBean(Terrain.class)
				.getGroundPosition(position), startUnderGround);
	}

	public House(ApplicationContext applicationContext, Vector3f position,
			boolean startUnderGround) {
		this.applicationContext = applicationContext;

		house = (Node) applicationContext.getBean(AssetManager.class)
				.loadModel("house2/house2.scene");

		house.setShadowMode(ShadowMode.Cast);
		applicationContext.getBean("rootNode", Node.class).attachChild(house);

		if (startUnderGround) {
			position.subtract(Vector3f.UNIT_Y.mult(getHeight()));
		}

		house.setLocalTranslation(position);

		house.setLocalScale(getWidth());
		ModelHelper.setColorToGeometry(house, ColorRGBA.Black, "");

		control = new RigidBodyControl(0f);
		house.addControl(control);

		applicationContext.getBean(BulletAppState.class).getPhysicsSpace()
				.add(control);

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
			selectionNode = new SelectionNode(
					applicationContext.getBean(AssetManager.class));
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

	@Override
	public List<String> getStatusText() {
		if (isConstructed()) {
			return Arrays.asList("House", //
					"owner: " + owner.getName());
		} else {
			return Arrays.asList("House", //
					"owner: " + owner.getName(), //
					"Under construction");
		}
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

	@Override
	public void finish() {
		owner.updateHousingLimit();
	}

	@Override
	public String getIconName() {
		return "house";
	}

}
