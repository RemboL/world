package pl.rembol.jme3.world;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.terrain.geomipmap.TerrainPatch;
import com.jme3.terrain.geomipmap.picking.TerrainPickData;

public class ShootListener implements ActionListener {

	private Camera cam;
	private Terrain terrain;

	public ShootListener(Camera cam, Terrain terrain) {
		this.cam = cam;
		this.terrain = terrain;
	}

	public void onAction(String name, boolean keyPressed, float tpf) {
		if (name.equals("Shoot") && !keyPressed) {
			Ray ray = new Ray(cam.getLocation(), cam.getDirection());

			CollisionResults results = new CollisionResults();
			terrain.getTerrain().collideWith(ray, results);
			CollisionResult collision = results.getClosestCollision();
			if (collision != null) {
				Vector3f point = collision.getContactPoint();

				Vector3f terrainPoint = new Vector3f();
				terrain.getTerrain().worldToLocal(point, terrainPoint);

				terrainPoint = terrainPoint.divide(terrain.getTerrainSize());

				terrain.addHill(new Vector2f(terrainPoint.getX() + .5f,
						.5f - terrainPoint.getZ()), .1f);
				List<TerrainPickData> newResults = new ArrayList<TerrainPickData>();
				terrain.getTerrain().findPick(ray, newResults );
				System.out.println("@@ "+newResults);
//				List<TerrainPatch> holder = new ArrayList<>();
//				terrain.getTerrain().getAllTerrainPatches(holder);
//				for(TerrainPatch patch : holder) {
//					patch.collideWith(other, results)
//				}
			}

		}
	}

	public TerrainPatch extractPatch(TerrainPickData pick) {
		Field patchField;
		try {
			patchField = TerrainPickData.class.getDeclaredField("targetPatch");
			patchField.setAccessible(true);
			return (TerrainPatch) patchField.get(pick);
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
