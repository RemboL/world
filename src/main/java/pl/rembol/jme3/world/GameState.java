package pl.rembol.jme3.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import pl.rembol.jme3.input.state.SelectionManager;
import pl.rembol.jme3.player.Player;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.house.House;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.warehouse.Warehouse;

import com.jme3.collision.Collidable;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class GameState {

	private GameState() {
	}

	private static GameState instance = new GameState();

	private SelectionManager selectionManager;

	public static GameState get() {
		return instance;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

	private Map<String, Selectable> selectables = new HashMap<>();

	public List<? extends Collidable> getSelectablesNodes() {
		List<Collidable> collidables = new ArrayList<>();

		for (Selectable selectable : selectables.values()) {
			collidables.add(selectable.getNode());
		}
		return collidables;
	}

	public void register(Selectable selectable) {
		String key = UUID.randomUUID().toString();
		selectables.put(key, selectable);
		selectable.getNode().setUserData("selectable", key);
	}

	public void unregister(Selectable selectable) {
		System.out.println("#### unregister");
		selectables.remove(selectable.getNode().getUserData("selectable"));
		selectable.getNode().setUserData("selectable", null);
		selectionManager.deselect(selectable);
	}

	public Selectable getSelectable(Node node) {
		if (node != null && node.getUserData("selectable") != null) {
			return selectables.get(node.getUserData("selectable").toString());
		}
		return null;
	}

	public List<Building> getHousesByOwner(Player player) {
		return selectables.values().stream()
				.filter(selectable -> House.class.isInstance(selectable))
				.map(selectable -> House.class.cast(selectable))
				.filter(house -> house.getOwner().equals(player))
				.filter(house -> house.isConstructed())
				.collect(Collectors.toList());
	}

	public List<BallMan> getBallMenByOwner(Player player) {
		return selectables.values().stream()
				.filter(selectable -> BallMan.class.isInstance(selectable))
				.map(selectable -> BallMan.class.cast(selectable))
				.filter(ballMan -> ballMan.getOwner().equals(player))
				.collect(Collectors.toList());
	}

	public List<Warehouse> getWarehousesByOwner(Player player) {
		return selectables.values().stream()
				.filter(selectable -> Warehouse.class.isInstance(selectable))
				.map(selectable -> Warehouse.class.cast(selectable))
				.filter(warehouse -> warehouse.getOwner().equals(player))
				.filter(warehouse -> warehouse.isConstructed())
				.collect(Collectors.toList());
	}

	public List<Selectable> getSelectableByPosition(Vector3f start,
			Vector3f stop) {
		float minX = Math.min(start.x, stop.x);
		float maxX = Math.max(start.x, stop.x);
		float minZ = Math.min(start.z, stop.z);
		float maxZ = Math.max(start.z, stop.z);

		return selectables
				.values()
				.stream()
				.filter(selectable -> selectable.getNode()
						.getWorldTranslation().x >= minX)
				.filter(selectable -> selectable.getNode()
						.getWorldTranslation().x <= maxX)
				.filter(selectable -> selectable.getNode()
						.getWorldTranslation().z > minZ)
				.filter(selectable -> selectable.getNode()
						.getWorldTranslation().z <= maxZ)
				.collect(Collectors.toList());
	}

	public boolean isSpaceFreeWithBuffer(Vector3f position, float width) {
		return isSpaceFree(position, width + 2.5f);
	}

	public boolean isSpaceFree(Vector3f position, float width) {
		return !selectables
				.values()
				.stream()
				.anyMatch(
						selectable -> isColliding(selectable, position, width));
	}

	private boolean isColliding(Selectable selectable, Vector3f position,
			float width) {
		if (selectable.getNode().getWorldTranslation().x
				+ selectable.getWidth() <= position.x - width) {
			return false;
		}
		if (selectable.getNode().getWorldTranslation().x
				- selectable.getWidth() >= position.x + width) {
			return false;
		}
		if (selectable.getNode().getWorldTranslation().z
				+ selectable.getWidth() <= position.z - width) {
			return false;
		}
		if (selectable.getNode().getWorldTranslation().z
				- selectable.getWidth() >= position.z + width) {
			return false;
		}

		return true;
	}
}
