package pl.rembol.jme3.world;

import java.util.ArrayList;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.HillHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class Terrain {

	private TerrainQuad terrain;
	private Material mat_terrain;
	private Texture alphaMap;
	private AlphaMapManipulator manipulator = new AlphaMapManipulator();

	public Terrain(AssetManager assetManager, Node rootNode, Camera camera,
			int size) throws Exception {
		createMaterials(assetManager);

		FlatHeightMap heightmap = new FlatHeightMap(size);
//		HillHeightMap heightmap = new HillHeightMap(size, 1000, 10f, 100f);

		int patchSize = 129;
		terrain = new TerrainQuad("my terrain", patchSize, size + 1,
				heightmap.getHeightMap());

		terrain.setMaterial(mat_terrain);
		terrain.setLocalTranslation(0, -100, 0);
		terrain.setLocalScale(2f, 1f, 2f);
		rootNode.attachChild(terrain);

		/** 5. The LOD (level of detail) depends on were the camera is: */
		TerrainLodControl control = new TerrainLodControl(terrain, camera);
		terrain.addControl(control);
	}

	private void createMaterials(AssetManager assetManager) {
		/** 1. Create terrain material and load four textures into it. */
		mat_terrain = new Material(assetManager,
				"Common/MatDefs/Terrain/Terrain.j3md");

		/** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
		alphaMap = assetManager.loadTexture("red.jpg");
		mat_terrain.setTexture("Alpha", alphaMap);

		/** 1.2) Add GRASS texture into the red layer (Tex1). */
		Texture grass = assetManager
				.loadTexture("Textures/Terrain/splat/grass.jpg");
		grass.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex1", grass);
		mat_terrain.setFloat("Tex1Scale", 64f);

		/** 1.3) Add DIRT texture into the green layer (Tex2) */
		Texture dirt = assetManager
				.loadTexture("Textures/Terrain/splat/dirt.jpg");
		dirt.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex2", dirt);
		mat_terrain.setFloat("Tex2Scale", 32f);

		/** 1.4) Add ROAD texture into the blue layer (Tex3) */
		Texture rock = assetManager
				.loadTexture("Textures/Terrain/splat/road.jpg");
		rock.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex3", rock);
		mat_terrain.setFloat("Tex3Scale", 128f);
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
