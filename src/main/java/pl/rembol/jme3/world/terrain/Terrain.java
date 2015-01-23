package pl.rembol.jme3.world.terrain;

import java.util.ArrayList;
import java.util.List;

import pl.rembol.jme3.world.GameRunningAppState;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
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
	private RigidBodyControl terrainBodyControl;
	private GameRunningAppState appState;

	public Terrain(Camera camera, int size, GameRunningAppState appState) {
		this.appState = appState;

		createMaterials(appState.getAssetManager());

		AbstractHeightMap heightmap = new FlatHeightMap(size);
		try {
			heightmap = new HillHeightMap(size, 10000, 10f, 20f);
			heightmap.normalizeTerrain(30f);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int patchSize = 129;
		terrain = new TerrainQuad("my terrain", patchSize, size + 1,
				heightmap.getHeightMap());
		terrain.setShadowMode(ShadowMode.Receive);

		terrain.setMaterial(mat_terrain);
		terrain.setLocalTranslation(0, 0, 0);
		terrain.setLocalScale(2f, 1f, 2f);
		appState.getRootNode().attachChild(terrain);

		TerrainLodControl control = new TerrainLodControl(terrain, camera);
		terrain.addControl(control);

		CollisionShape sceneShape = CollisionShapeFactory
				.createMeshShape((Node) terrain);
		terrainBodyControl = new RigidBodyControl(sceneShape, 0);
		appState.getBulletAppState().getPhysicsSpace().add(terrainBodyControl);
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
		mat_terrain.setFloat("DiffuseMap_1_scale", 64f);

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

	public boolean isTerrainSmooth(Vector2f start, Vector2f end) {
		int minX = Math.min(Math.round(start.x), Math.round(end.x));
		int maxX = Math.max(Math.round(start.x), Math.round(end.x));
		int minY = Math.min(Math.round(start.y), Math.round(end.y));
		int maxY = Math.max(Math.round(start.y), Math.round(end.y));

		float height = terrain.getHeight(new Vector2f(start.x, start.y));

		for (int x = minX; x <= maxX; ++x) {
			for (int y = minY; y <= maxY; ++y) {
				if (Math.abs(height - terrain.getHeight(new Vector2f(x, y))) > .1f) {
					return false;
				}
			}
		}

		resetTerrain();

		return true;
	}

	public void smoothenTerrain(Vector2f start, Vector2f end, int border,
			float maxDiff) {

		terrain.setLocked(false);

		int minX = Math.min(Math.round(start.x), Math.round(end.x));
		int maxX = Math.max(Math.round(start.x), Math.round(end.x));
		int minY = Math.min(Math.round(start.y), Math.round(end.y));
		int maxY = Math.max(Math.round(start.y), Math.round(end.y));

		List<Vector2f> positions = new ArrayList<>();
		List<Float> heights = new ArrayList<>();

		float averageHeight = calculateAverageHeightOfBorder(minX, maxX, minY,
				maxY);

		for (int x = minX - border; x <= maxX + border; ++x) {
			for (int y = minY - border; y <= maxY + border; ++y) {

				if (x > -terrain.getTerrainSize()
						&& x < terrain.getTerrainSize()
						&& y > -terrain.getTerrainSize()
						&& y < terrain.getTerrainSize()) {
					positions.add(new Vector2f(x, y));

					float degree = getSmoothedRectangleDegree(minX, maxX, minY,
							maxY, border, x, y);
					float diff = (averageHeight - terrain
							.getHeight(new Vector2f(x, y))) * degree;

					if (Math.abs(diff) > maxDiff) {
						diff = Math.signum(diff) * maxDiff;
					}

					manipulator.shiftPixelColor(alphaMap,
							getAlphaMapPosition(new Vector2f(x, y)),
							Math.abs(diff), ColorRGBA.Green);

					heights.add(diff);
				}
			}
		}
		terrain.adjustHeight(positions, heights);
		terrain.setLocked(true);

		manipulator.update(alphaMap);

		resetTerrain();
	}

	private Vector2f getAlphaMapPosition(Vector2f position) {
		return new Vector2f(position.divide(terrain.getTerrainSize() * 2).x,
				-position.divide(terrain.getTerrainSize() * 2).y)
				.add(new Vector2f(.5f, .5f));
	}

	public void smoothenTerrain(Vector2f start, Vector2f end) {

		manipulator.addBlop(alphaMap, start, 5f, ColorRGBA.Green);

		terrain.setLocked(false);

		int minX = Math.min(Math.round(start.x), Math.round(end.x));
		int maxX = Math.max(Math.round(start.x), Math.round(end.x));
		int minY = Math.min(Math.round(start.y), Math.round(end.y));
		int maxY = Math.max(Math.round(start.y), Math.round(end.y));

		List<Vector2f> positions = new ArrayList<>();
		List<Float> heights = new ArrayList<>();

		float averageHeight = calculateAverageHeightOfBorder(minX, maxX, minY,
				maxY);

		for (int x = minX; x <= maxX; ++x) {
			for (int y = minY; y <= maxY; ++y) {

				if (x > -terrain.getTerrainSize()
						&& x < terrain.getTerrainSize()
						&& y > -terrain.getTerrainSize()
						&& y < terrain.getTerrainSize()) {
					positions.add(new Vector2f(x, y));

					float diff = averageHeight
							- terrain.getHeight(new Vector2f(x, y));

					heights.add(diff);
				}
			}
		}

		terrain.adjustHeight(positions, heights);
		terrain.setLocked(true);

	}

	private float getSmoothedRectangleDegree(int minX, int maxX, int minY,
			int maxY, int border, int x, int y) {
		return getSmoothedLineDegree(minX, maxX, x, border)
				* getSmoothedLineDegree(minY, maxY, y, border);
	}

	private float getSmoothedLineDegree(int min, int max, int current,
			int border) {
		if (current < min - border || current > max + border) {
			return 0;
		}

		if (current >= min - border && current < min) {
			return 1 - (min - current) * (min - current)
					/ (1f * border * border);
		}

		if (current > max && current <= max + border) {
			return 1 - (current - max) * (current - max)
					/ (1f * border * border);
		}

		return 1;
	}

	private float calculateAverageHeightOfBorder(int minX, int maxX, int minY,
			int maxY) {
		float heightSum = 0;
		int heightCount = 0;
		for (int x = minX; x <= maxX; ++x) {
			heightSum += terrain.getHeight(new Vector2f(x, minY));
			heightSum += terrain.getHeight(new Vector2f(x, maxY));
			heightCount += 2;
		}
		for (int y = minY; y <= maxY; ++y) {
			heightSum += terrain.getHeight(new Vector2f(minX, y));
			heightSum += terrain.getHeight(new Vector2f(maxX, y));
			heightCount += 2;
		}

		return heightSum / heightCount;
	}

	private void resetTerrain() {
		CollisionShape sceneShape = CollisionShapeFactory
				.createMeshShape((Node) terrain);
		appState.getBulletAppState().getPhysicsSpace()
				.remove(terrainBodyControl);
		terrainBodyControl = new RigidBodyControl(sceneShape, 0);
		appState.getBulletAppState().getPhysicsSpace().add(terrainBodyControl);
	}

	private void addSmoothHillHeightMap(Vector2f position, float radius) {
		terrain.setLocked(false);

		position = new Vector2f(position.x - .5f, .5f - position.y)
				.mult(terrain.getTerrainSize() * 2);

		int hillCenterX = Math.round(position.x);
		int hillCenterY = Math.round(position.y);

		int intRadius = Math.round(terrain.getTerrainSize() * radius);
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

	public Vector3f getGroundPosition(Vector2f position) {
		return new Vector3f(position.x, terrain.getHeight(new Vector2f(
				position.x, position.y)) + terrain.getLocalTranslation().y,
				position.y);
	}

	public Vector3f getGroundPosition(Vector3f position) {
		return new Vector3f(position.x, terrain.getHeight(new Vector2f(
				position.x, position.z)) + terrain.getLocalTranslation().y,
				position.z);
	}
}
