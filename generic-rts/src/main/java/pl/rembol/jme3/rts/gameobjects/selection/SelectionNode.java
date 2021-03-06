package pl.rembol.jme3.rts.gameobjects.selection;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Torus;
import pl.rembol.jme3.rts.RtsGameState;

public class SelectionNode extends Node {

    public SelectionNode(RtsGameState gameState) {
        setShadowMode(ShadowMode.Off);
        Geometry geometry = new Geometry("selection", new Torus(20, 4, .05f,
                1.5f));
        geometry.rotate(FastMath.HALF_PI, 0, 0);

        Material selectionMaterial = new Material(gameState.assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        selectionMaterial.setBoolean("UseMaterialColors", true);
        selectionMaterial.setColor("Diffuse", ColorRGBA.Green);
        selectionMaterial.setColor("Specular", ColorRGBA.Green);
        selectionMaterial.setColor("Ambient", ColorRGBA.Green.mult(.1f));
        selectionMaterial.setFloat("Shininess", 32f);
        geometry.setMaterial(selectionMaterial);

        attachChild(geometry);
    }

}
