package pl.rembol.jme3.utils;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Materials {

    public static void setAlpha(Spatial spatial, float alpha) {
        setAlpha(spatial, "Color", alpha);
    }

    public static void setAlpha(Spatial spatial, String colorName, float alpha) {
        if (spatial instanceof Node) {
            setAlphaToNode((Node) spatial, colorName, alpha);
        } else if (spatial instanceof Geometry) {
            setAlphaToGemoetry((Geometry) spatial, colorName, alpha);
        }
    }

    private static void setAlphaToNode(Node node, String colorName, float alpha) {
        for (Spatial spatial : node.getChildren()) {
            setAlpha(spatial, colorName, alpha);
        }
    }

    private static void setAlphaToGemoetry(Geometry geometry, String colorName, float alpha) {
        Material material = geometry.getMaterial();
        if (!(material.getAdditionalRenderState().getBlendMode() == RenderState.BlendMode.Alpha)) {
            material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        }
        rewriteAlpha(material, colorName, alpha);

    }

    private static void rewriteAlpha(Material material, String colorName, float alpha) {
        ColorRGBA color = ((ColorRGBA) material.getParam(colorName).getValue()).clone();
        color.a = alpha;
        material.setColor(colorName, color);
    }

}
