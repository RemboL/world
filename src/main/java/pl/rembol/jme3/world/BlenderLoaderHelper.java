package pl.rembol.jme3.world;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class BlenderLoaderHelper {

	/**
	 * By default, .blend models have material ambient value set to World
	 * ambient lighting color, while JME material ambient value corresponds with
	 * color of material while lit with ambient lighting.<br>
	 * <br>
	 * To mitigate this difference, the material's diffuse color (which is the
	 * proper color of .blend model) is rewritten to ambient color
	 * 
	 * @param node
	 * @return
	 */
	public static Node rewriteDiffuseToAmbient(Node node) {
		rewriteNode(node);

		return node;
	}

	private static void rewriteNode(Node node) {
		for (Spatial spatial : node.getChildren()) {
			if (spatial instanceof Node) {
				rewriteNode((Node) spatial);
			} else if (spatial instanceof Geometry) {
				rewriteGeometry((Geometry) spatial);
			}
		}
	}

	private static void rewriteGeometry(Geometry geometry) {
		Material material = geometry.getMaterial();

		ColorRGBA color = (ColorRGBA) material.getParam("Diffuse").getValue();
		material.setColor("Ambient", color);
	}

}
