package pl.rembol.jme3.world.ballman.hunger;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.world.ballman.BallMan;

public class HungerIndicator extends Node {

    private Node node;

    private Material material;

    private static final ColorRGBA GREEN = new ColorRGBA(.5f, 1f, .5f, .2f);

    private static final ColorRGBA YELLOW = new ColorRGBA(1f, 1f, 0f, .2f);

    private static final ColorRGBA RED = new ColorRGBA(1f, 0f, 0f, .2f);

    public HungerIndicator(RtsGameState gameState, BallMan ballMan, float hungerFactor) {

        node = (Node) gameState.assetManager.loadModel("hunger_indicator/hunger_indicator.mesh.xml");
        node.setShadowMode(ShadowMode.Off);

        this.attachChild(node);

        ballMan.getNode().attachChild(this);
        this.setLocalTranslation(Vector3f.UNIT_Y.mult(5f));

        material = new Material(gameState.assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        hunger(hungerFactor);

        setMaterial(material);
        node.setQueueBucket(Bucket.Transparent);
    }

    public void hunger(float hungerFactor) {
        ColorRGBA newColor = new ColorRGBA();
        if (hungerFactor > .1f) {
            newColor.interpolateLocal(YELLOW, GREEN, (hungerFactor - .1f) * 10);
        } else {
            newColor.interpolateLocal(RED, YELLOW, hungerFactor * 10);
        }

        material.setColor("Color", newColor);

    }

}
