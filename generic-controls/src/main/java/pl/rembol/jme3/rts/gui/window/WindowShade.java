package pl.rembol.jme3.rts.gui.window;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import pl.rembol.jme3.rts.GameState;

public class WindowShade extends Node {

    private static Material muchLighterShade;

    private static Material slightlyLighterShade;

    private static Material slightlyDarkerShade;

    private static Material muchDarkerShade;

    private static Material muchMuchDarkerShade;

    private final GameState gameState;

    private final int width;

    private final int height;

    private final int depth;

    public static final int EDGE = 5;

    public static final int TOP_FRAME = 42;

    public static final int FRAME = 20;

    public WindowShade(GameState gameState, int width, int height, int depth) {
        this.gameState = gameState;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public void initAllShades() {
        initOuterShades();

        initInnerShades();

        initShade(getMuchMuchDarkerShade(),
                new Vector3f(FRAME, FRAME, depth),
                new Vector3f(width - FRAME, FRAME, depth),
                new Vector3f(width - FRAME, height - TOP_FRAME, depth),
                new Vector3f(FRAME, height - TOP_FRAME, depth));
    }

    public void initBox(Vector2f start, Vector2f end, boolean convex) {
        initBox(start, end, convex, false);
    }
    
    public void initBox(Vector2f start, Vector2f end, boolean convex, boolean outerShade) {
        float startX = Math.min(start.x, end.x);
        float endX = Math.max(start.x, end.x);
        float startY = Math.min(start.y, end.y);
        float endY = Math.max(start.y, end.y);
        
        if (outerShade) {
            startX -= EDGE;
            startY -= EDGE;
            endX += EDGE;
            endY += EDGE;
        }

        initShade(convex ? getMuchDarkerShade() : getMuchLighterShade(),
                new Vector3f(startX, startY, depth),
                new Vector3f(startX + EDGE, startY + EDGE, depth),
                new Vector3f(startX + EDGE, endY - EDGE, depth),
                new Vector3f(startX, endY, depth));
        initShade(convex ? getSlightlyDarkerShade() : getSlightlyLighterShade(),
                new Vector3f(startX, startY, depth),
                new Vector3f(endX, startY, depth),
                new Vector3f(endX - EDGE, startY + EDGE, depth),
                new Vector3f(startX + EDGE, startY + EDGE, depth));
        initShade(convex ? getMuchLighterShade() : getMuchDarkerShade(),
                new Vector3f(endX, startY, depth),
                new Vector3f(endX, endY, depth),
                new Vector3f(endX - EDGE, endY - EDGE, depth),
                new Vector3f(endX - EDGE, startY + EDGE, depth));
        initShade(convex ? getSlightlyLighterShade() : getSlightlyDarkerShade(),
                new Vector3f(startX, endY, depth),
                new Vector3f(startX + EDGE, endY - EDGE, depth),
                new Vector3f(endX - EDGE, endY - EDGE, depth),
                new Vector3f(endX, endY, depth));
    }

    public void initOuterShades() {
        initBox(
                new Vector2f(0, 0),
                new Vector2f(width, height),
                true);
    }

    public void initInnerShades() {
        initBox(
                new Vector2f(FRAME - EDGE, FRAME - EDGE),
                new Vector2f(width - FRAME + EDGE, height - TOP_FRAME + EDGE),
                false);
    }

    public void initShade(Material material, Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f vertex4) {
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
