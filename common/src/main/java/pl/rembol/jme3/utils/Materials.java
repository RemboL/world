package pl.rembol.jme3.utils;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Materials {

    public static void setAlpha(Spatial spatial, float alpha) {
        if (spatial instanceof Node) {
            setAlphaToNode((Node) spatial, alpha);
        } else if (spatial instanceof Geometry) {
            setAlphaToGemoetry((Geometry) spatial, alpha);
        }
    }

    private static void setAlphaToNode(Node node, float alpha) {
        for (Spatial spatial : node.getChildren()) {
            setAlpha(spatial, alpha);
        }
    }

    private static void setAlphaToGemoetry(Geometry geometry, float alpha) {
        Material material = geometry.getMaterial();
        if (!(material.getAdditionalRenderState().getBlendMode() == RenderState.BlendMode.Alpha)) {
            material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        }
        rewriteAlpha(material, alpha);

    }

    private static void rewriteAlpha(Material material, float alpha) {
        ColorRGBA color = ((ColorRGBA) material.getParam("Color").getValue());
        color.a = alpha;
        material.setColor("Color", color);
    }

}
