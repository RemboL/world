package pl.rembol.jme3.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pl.rembol.jme3.input.state.SelectionManager;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.collision.Collidable;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;

public class GameState {

	private GameState() {
	}

	private static GameState instance = new GameState();

	private SelectionManager selectionManager;

	public static GameState get() {
		return instance;
	}

	private Terrain terrain;

	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

	private Map<String, Selectable> selectables = new HashMap<>();

	public TerrainQuad getTerrainQuad() {
		return terrain.getTerrain();
	}

	public Terrain getTerrain() {
		return terrain;
	}

	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}

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
		selectable.getNode().setUserData("selectable", null);
		selectables.remove(selectable);
		selectionManager.deselect(selectable);
	}

	public Selectable getSelectable(Node node) {
		if (node != null && node.getUserData("selectable") != null) {
			return selectables.get(node.getUserData("selectable").toString());
		}
		return null;
	}

}
