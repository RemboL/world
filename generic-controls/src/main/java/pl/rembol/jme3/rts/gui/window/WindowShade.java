package pl.rembol.jme3.rts.gui.window;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import pl.rembol.jme3.rts.GameState;

class WindowShade extends Node {

    private static Material muchLighterShade;
    private static Material slightlyLighterShade;
    private static Material slightlyDarkerShade;
    private static Material muchDarkerShade;
    private static Material muchMuchDarkerShade;

    private final GameState gameState;

    private static final int EDGE = 5;

    private static final int TOP_FRAME = 42;

    private static final int FRAME = 20;

    public WindowShade(GameState gameState, int width, int height, int depth) {
        this.gameState = gameState;
        initShades(width, height, depth);

    }

    private void initShades(int width, int height, int depth) {
        initShade(getMuchDarkerShade(), new Vector3f(0, 0, depth), new Vector3f(EDGE, EDGE, depth), new Vector3f(EDGE, height - EDGE, depth), new Vector3f(0, height, depth));
        initShade(getSlightlyDarkerShade(), new Vector3f(0, 0, depth), new Vector3f(width, 0, depth), new Vector3f(width - EDGE, EDGE, depth), new Vector3f(EDGE, EDGE, depth));
        initShade(getMuchLighterShade(), new Vector3f(width, 0, depth), new Vector3f(width, height, depth), new Vector3f(width - EDGE, height - EDGE, depth), new Vector3f(width - EDGE, EDGE, depth));
        initShade(getSlightlyLighterShade(), new Vector3f(0, height, depth), new Vector3f(EDGE, height - EDGE, depth), new Vector3f(width - EDGE, height - EDGE, depth), new Vector3f(width, height, depth));

        initShade(getMuchLighterShade(), new Vector3f(FRAME - EDGE, FRAME - EDGE, depth), new Vector3f(FRAME, FRAME, depth), new Vector3f(FRAME, height - TOP_FRAME, depth), new Vector3f(FRAME - EDGE, height - TOP_FRAME + EDGE, depth));
        initShade(getSlightlyLighterShade(), new Vector3f(FRAME - EDGE, FRAME - EDGE, depth), new Vector3f(width - FRAME + EDGE, FRAME - EDGE, depth), new Vector3f(width - FRAME, FRAME, depth), new Vector3f(FRAME, FRAME, depth));
        initShade(getMuchDarkerShade(), new Vector3f(width - FRAME + EDGE, FRAME - EDGE, depth), new Vector3f(width - FRAME + EDGE, height - TOP_FRAME + EDGE, depth), new Vector3f(width - FRAME, height - TOP_FRAME, depth), new Vector3f(width - FRAME, FRAME, depth));
        initShade(getSlightlyDarkerShade(), new Vector3f(FRAME - EDGE, height - TOP_FRAME + EDGE, depth), new Vector3f(FRAME, height - TOP_FRAME, depth), new Vector3f(width - FRAME, height - TOP_FRAME, depth), new Vector3f(width - FRAME + EDGE, height - TOP_FRAME + EDGE, depth));

        initShade(getMuchMuchDarkerShade(), new Vector3f(FRAME, FRAME, depth), new Vector3f(width - FRAME, FRAME, depth), new Vector3f(width - FRAME, height - TOP_FRAME, depth), new Vector3f(FRAME, height - TOP_FRAME, depth));
    }

    private void initShade(Material material, Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f vertex4) {
        Spatial spatial = new Geometry("shade", new QuadrangleMesh(vertex1, vertex2, vertex3, vertex4));
        spatial.setQueueBucket(RenderQueue.Bucket.Gui);
        spatial.setCullHint(Spatial.CullHint.Never);

        spatial.setMaterial(material);

        attachChild(spatial);
    }

    private Material initMaterial(ColorRGBA colorRGBA) {
        Material mat = new Material(gameState.assetManager, "Common/MatDefs/Gui/Gui.j3md");
        mat.setColor("Color", colorRGBA);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return mat;
    }

    private Material getMuchLighterShade() {
        if (muchLighterShade == null) {
            muchLighterShade = initMaterial(new ColorRGBA(1F, 1F, 1F, .4F));
        }

        return muchLighterShade;
    }

    private Material getSlightlyLighterShade() {
        if (slightlyLighterShade == null) {
            slightlyLighterShade = initMaterial(new ColorRGBA(1F, 1F, 1F, .2F));
        }

        return slightlyLighterShade;
    }

    private Material getSlightlyDarkerShade() {
        if (slightlyDarkerShade == null) {
            slightlyDarkerShade = initMaterial(new ColorRGBA(0F, 0F, 0F, .2F));
        }

        return slightlyDarkerShade;
    }

    private Material getMuchDarkerShade() {
        if (muchDarkerShade == null) {
            muchDarkerShade = initMaterial(new ColorRGBA(0F, 0F, 0F, .4F));
        }

        return muchDarkerShade;
    }

    private Material getMuchMuchDarkerShade() {
        if (muchMuchDarkerShade == null) {
            muchMuchDarkerShade = initMaterial(new ColorRGBA(0F, 0F, 0F, .8F));
        }

        return muchMuchDarkerShade;
    }


}
