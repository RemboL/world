package pl.rembol.jme3.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.Collidable;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;

public class GameState {

	private GameState() {
	}

	private static GameState instance = new GameState();

	public static GameState get() {
		return instance;
	}

	private Node rootNode;

	private AssetManager assetManager;

	private BulletAppState bulletAppState;

	private Terrain terrain;

	private List<Selectable> selected = new ArrayList<>();

	private Map<String, Selectable> selectables = new HashMap<>();

	public Node getRootNode() {
		return rootNode;
	}

	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	public BulletAppState getBulletAppState() {
		return bulletAppState;
	}

	public void setBulletAppState(BulletAppState bulletAppState) {
		this.bulletAppState = bulletAppState;
	}

	public TerrainQuad getTerrain() {
		return terrain.getTerrain();
	}

	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}

	public List<? extends Collidable> getCollidables() {
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
		deselect(selectable);
	}

	public Selectable getSelectable(Node node) {
		if (node != null && node.getUserData("selectable") != null) {
			return selectables.get(node.getUserData("selectable").toString());
		}
		return null;
	}

	public void select(Selectable selectable) {
		for (Selectable previouslySelected : selected) {
			previouslySelected.deselect();
		}

		selected.clear();

		selectable.select();
		selected.add(selectable);
	}

	public void deselect(Selectable selectable) {
		selected.remove(selectable);
		selectable.deselect();
	}

	public List<Selectable> getSelected() {
		return selected;
	}

}
