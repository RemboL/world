package pl.rembol.jme3.rts;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class ModelHelper {

    public static Spatial rewriteDiffuseToAmbient(Spatial spatial) {
        if (spatial instanceof Node) {
            return rewriteDiffuseToAmbient((Node) spatial);
        } else if (spatial instanceof Geometry) {
            rewriteGeometry((Geometry) spatial);
            return spatial;
        } else return spatial;
    }
    
    /**
     * By default, .blend models have material ambient value set to World
     * ambient lighting color, while JME material ambient value corresponds with
     * color of material while lit with ambient lighting.<br>
     * <br>
     * To mitigate this difference, the material's diffuse color (which is the
     * proper color of .blend model) is rewritten to ambient color
     *
     * @param node node to be rewritten
     * @return node
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

    public static void setColorToGeometry(Node node, ColorRGBA color,
                                          String name) {
        for (Spatial spatial : node.getChildren()) {
            if (spatial instanceof Node) {
                setColorToGeometry((Node) spatial, color, name);
            } else if (spatial instanceof Geometry) {
                setColorToMaterial((Geometry) spatial, color, name);
            }
        }
    }

    private static void setColorToMaterial(Geometry geometry, ColorRGBA color,
                                           String name) {
        if (name.equals(geometry.getName())) {
            geometry.getMaterial().setColor("Diffuse", color);
            geometry.getMaterial().setColor("Ambient", color);
        }
    }

}
