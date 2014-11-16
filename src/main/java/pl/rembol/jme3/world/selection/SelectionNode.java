package pl.rembol.jme3.world.selection;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

public class SelectionNode extends Node {

	public SelectionNode(AssetManager assetManager) {
		setShadowMode(ShadowMode.Off);
		Geometry sphereGeometry = new Geometry("selection", new Sphere(5, 12,
				.5f));

		Material selectionMaterial = new Material(assetManager,
				"Common/MatDefs/Light/Lighting.j3md");
		selectionMaterial.setBoolean("UseMaterialColors", true);
		selectionMaterial.setColor("Diffuse", ColorRGBA.Green);
		selectionMaterial.setColor("Specular", ColorRGBA.Green);
		selectionMaterial.setColor("Ambient", ColorRGBA.Green.mult(.1f));
		selectionMaterial.setFloat("Shininess", 32f);
		sphereGeometry.setMaterial(selectionMaterial);

		attachChild(sphereGeometry);
	}

}
