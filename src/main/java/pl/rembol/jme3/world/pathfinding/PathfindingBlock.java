package pl.rembol.jme3.world.pathfinding;

import java.util.Random;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

class PathfindingBlock {

	private static Material greenMaterial = null;
	private static Material redMaterial = null;

	Spatial spatial;

	private boolean isFree = true;

	public boolean isFree() {
		return isFree;
	}

	public void setFree(int x, int y, boolean isFree,
			ApplicationContext applicationContext) {
		this.isFree = isFree;

//		initMaterials(applicationContext.getBean(AssetManager.class));
//
//		if (spatial == null) {
//			spatial = new Geometry("ball", new Sphere(5, 5, .2f));
//			spatial.setLocalTranslation(applicationContext
//					.getBean(Terrain.class)
//					.getGroundPosition(new Vector2f(x, y))
//					.add(Vector3f.UNIT_Y.mult(10)).add(Vector3f.UNIT_Y.mult(new Random().nextFloat())));
//			spatial.setMaterial(isFree ? greenMaterial : redMaterial);
//			applicationContext.getBean("rootNode", Node.class).attachChild(
//					spatial);
//		} else {
//			spatial.setMaterial(isFree ? greenMaterial : redMaterial);
//		}
	}

	private static void initMaterials(AssetManager assetManager) {
		if (greenMaterial == null) {
			greenMaterial = new Material(assetManager,
					"Common/MatDefs/Misc/Unshaded.j3md");
			greenMaterial.getAdditionalRenderState().setBlendMode(
					BlendMode.Alpha);
			greenMaterial.setColor("Color", new ColorRGBA(.5f, 1f, .5f, 1f));
		}

		if (redMaterial == null) {
			redMaterial = new Material(assetManager,
					"Common/MatDefs/Misc/Unshaded.j3md");
			redMaterial.getAdditionalRenderState()
					.setBlendMode(BlendMode.Alpha);
			redMaterial.setColor("Color", new ColorRGBA(1f, .5f, .5f, 1f));
		}
	}

}
