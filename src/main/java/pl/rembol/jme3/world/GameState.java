package pl.rembol.jme3.world;

import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;

public class GameState {

	private static Node rootNode;

	private static AssetManager assetManager;

	private static BulletAppState bulletAppState;

	private static Terrain terrain;

	public static Node getRootNode() {
		return rootNode;
	}

	public static void setRootNode(Node rootNode) {
		GameState.rootNode = rootNode;
	}

	public static AssetManager getAssetManager() {
		return assetManager;
	}

	public static void setAssetManager(AssetManager assetManager) {
		GameState.assetManager = assetManager;
	}

	public static BulletAppState getBulletAppState() {
		return bulletAppState;
	}

	public static void setBulletAppState(BulletAppState bulletAppState) {
		GameState.bulletAppState = bulletAppState;
	}

	public static Terrain getTerrain() {
		return terrain;
	}

	public static void setTerrain(Terrain terrain) {
		GameState.terrain = terrain;
	}

}
