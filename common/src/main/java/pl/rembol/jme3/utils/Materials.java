package pl.rembol.jme3.utils;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.function.Consumer;

public class Materials {

    public static void setAlpha(Spatial spatial, float alpha) {
        setAlpha(spatial, "Color", alpha);
    }

    public static void setAlpha(Spatial spatial, String colorName, float alpha) {
        modifyMaterials(spatial, material -> {
            if (!(material.getAdditionalRenderState().getBlendMode() == RenderState.BlendMode.Alpha)) {
                material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            }
            rewriteAlpha(material, colorName, alpha);

        });
    }

    private static void rewriteAlpha(Material material, String colorName, float alpha) {
        ColorRGBA color = ((ColorRGBA) material.getParam(colorName).getValue()).clone();
        color.a = alpha;
        material.setColor(colorName, color);
    }

    public static void modifyMaterials(Spatial spatial, Consumer<Material> modifier) {
        if (spatial instanceof Node) {
            modifyMaterialsToNode((Node) spatial, modifier);
        } else if (spatial instanceof Geometry) {
            modifyMaterialsToGeometry((Geometry) spatial, modifier);
        }
    }

    private static void modifyMaterialsToGeometry(Geometry geometry, Consumer<Material> modifier) {
        Material material = geometry.getMaterial();
        modifier.accept(material);
    }

    private static void modifyMaterialsToNode(Node node, Consumer<Material> modifier) {
        for (Spatial spatial : node.getChildren()) {
            modifyMaterials(spatial, modifier);
        }
    }

}
