package pl.rembol.jme3.world;

import java.util.ArrayList;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.HillHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class Terrain {

	private TerrainQuad terrain;
	private Material mat_terrain;
	private Texture alphaMap;
	private AlphaMapManipulator manipulator = new AlphaMapManipulator();

	public Terrain(AssetManager assetManager, Node rootNode, Camera camera,
			int size, BulletAppState bulletAppState) {
		createMaterials(assetManager);

		AbstractHeightMap heightmap = new FlatHeightMap(size);
		try {
			heightmap = new HillHeightMap(size, 10000, 10f, 20f);
			heightmap.normalizeTerrain(50f);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int patchSize = 129;
		terrain = new TerrainQuad("my terrain", patchSize, size + 1,
				heightmap.getHeightMap());
		terrain.setShadowMode(ShadowMode.CastAndReceive);

		terrain.setMaterial(mat_terrain);
		terrain.setLocalTranslation(0, -100, 0);
		terrain.setLocalScale(2f, 1f, 2f);
		rootNode.attachChild(terrain);

		TerrainLodControl control = new TerrainLodControl(terrain, camera);
		terrain.addControl(control);

		CollisionShape sceneShape = CollisionShapeFactory
				.createMeshShape((Node) terrain);
		RigidBodyControl terrainBodyControl = new RigidBodyControl(sceneShape,
				0);
		bulletAppState.getPhysicsSpace().add(terrainBodyControl);
		terrain.addControl(terrainBodyControl);
	}

	private void createMaterials(AssetManager assetManager) {
		mat_terrain = new Material(assetManager,
				"Common/MatDefs/Terrain/TerrainLighting.j3md");
		mat_terrain.setBoolean("useTriPlanarMapping", false);
		mat_terrain.setBoolean("WardIso", true);
		mat_terrain.setFloat("Shininess", 0);

		alphaMap = assetManager.loadTexture("red.jpg");
		mat_terrain.setTexture("AlphaMap", alphaMap);

		Texture grass = assetManager
				.loadTexture("Textures/Terrain/splat/grass.jpg");
		grass.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("DiffuseMap", grass);
		mat_terrain.setFloat("DiffuseMap_0_scale", 64f);

		Texture dirt = assetManager
				.loadTexture("Textures/Terrain/splat/dirt.jpg");
		dirt.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("DiffuseMap_1", dirt);
		mat_terrain.setFloat("DiffuseMap_1_scale", 32f);

		Texture rock = assetManager
				.loadTexture("Textures/Terrain/splat/road.jpg");
		rock.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("DiffuseMap_2", rock);
		mat_terrain.setFloat("DiffuseMap_2_scale", 128f);
	}

	public TerrainQuad getTerrain() {
		return terrain;
	}

	public int getTerrainSize() {
		return terrain.getTerrainSize();

	}

	public void addHill(Vector2f position, float radius) {
		manipulator.addBlop(alphaMap, position, radius, ColorRGBA.Green);

		addSmoothHillHeightMap(position, radius);
	}

	private void addSmoothHillHeightMap(Vector2f position, float radius) {
		terrain.setLocked(false);

		position = new Vector2f(position.x - .5f, .5f - position.y)
				.mult(terrain.getTerrainSize() * 2);

		int hillCenterX = Math.round(position.x);
		int hillCenterY = Math.round(position.y);

		int intRadius = Math.round(terrain.getTerrainSize() * radius);
		System.out.println(intRadius);
		int intRadiusSquared = intRadius * intRadius;

		List<Vector2f> positions = new ArrayList<>();
		List<Float> heights = new ArrayList<>();
		for (int pointDiffX = -intRadius; pointDiffX < intRadius; ++pointDiffX) {
			for (int pointDiffY = -intRadius; pointDiffY < intRadius; ++pointDiffY) {
				if (hillCenterX + pointDiffX > -terrain.getTerrainSize()
						&& hillCenterX + pointDiffX < terrain.getTerrainSize()
						&& hillCenterY + pointDiffY > -terrain.getTerrainSize()
						&& hillCenterY + pointDiffY < terrain.getTerrainSize()) {
					positions.add(new Vector2f(hillCenterX + pointDiffX,
							hillCenterY + pointDiffY));
					int distSquared = pointDiffX * pointDiffX + pointDiffY
							* pointDiffY;
					if (distSquared > intRadiusSquared) {
						heights.add(0f);
					} else {
						heights.add(10f - (10f * distSquared / intRadiusSquared));
					}
				}
			}
		}
		terrain.adjustHeight(positions, heights);
		terrain.setLocked(true);

	}
}
