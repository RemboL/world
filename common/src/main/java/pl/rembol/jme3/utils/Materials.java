package pl.rembol.jme3.utils;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Materials {

    public static void setAlpha(Node node, float alpha) {
        for (Spatial spatial : node.getChildren()) {
            if(spatial instanceof Node) {
                setAlpha((Node)spatial, alpha);
            } else if(spatial instanceof Geometry) {
                setAlpha((Geometry)spatial, alpha);
            }
        }
    }

    private static void setAlpha(Geometry geometry, float alpha) {
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
