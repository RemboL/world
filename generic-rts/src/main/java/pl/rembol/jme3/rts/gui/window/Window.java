package pl.rembol.jme3.rts.gui.window;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.jme3.ui.Picture;
import pl.rembol.jme3.geom.Vector2i;
import pl.rembol.jme3.rts.RtsGameState;

public class Window extends pl.rembol.jme3.game.gui.window.Window<RtsGameState> {

    private static final float WINDOW_FRAME_WIDTH = 800;

    private static final float WINDOW_FRAME_HEIGHT = 600;

    protected WindowShade windowShade;

    public Window(RtsGameState gameState, int width, int height) {
        super(gameState, "", new Vector2f(width, height));

        initFrame();

        windowShade = new WindowShade(gameState, width, height, -1);
        attachChild(windowShade);

        initShades();

        initCloseButton();
    }

    protected void initFrame() {
        Picture frame = new Picture("Window frame");
        frame.setImage(gameState.simpleApplication.getAssetManager(),
                "interface/" + gameState.themeName() + "/window_frame.jpg", true);
        frame.move(0, 0, -2);
        frame.setWidth(size.x);
        frame.setHeight(size.y);

        Quad quad = new Quad(1.0F, 1.0F, false);
        float textureBoundX = Math.min(size.x / WINDOW_FRAME_WIDTH, 1);
        float textureBoundY = Math.min(size.y / WINDOW_FRAME_HEIGHT, 1);
        quad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{0.0F, 0.0F, textureBoundX, 0.0F, textureBoundX, textureBoundY, 0.0F, textureBoundY});
        frame.setMesh(quad);


        attachChild(frame);
    }

    protected void initShades() {
        windowShade.initAllShades();
    }

    protected void initCloseButton() {
        CloseButton closeButton = new CloseButton(gameState, this);

        closeButton.setLocalTranslation(size.x - 37, size.y - 37, 0);
        attachChild(closeButton);
    }

    protected void onClickOutside() {
    }

    public void attachPictureWithShade(Spatial spatial, Vector3f location, Vector2i size) {
        windowShade.initBox(
                new Vector2f(location.x, location.y),
                new Vector2f(location.x + size.x, location.y + size.y),
                false, true);

        attachChild(spatial);
        spatial.setLocalTranslation(location);
    }

    protected Vector2f getInnerFrameTopLeftCorner() {
        return new Vector2f(WindowShade.FRAME, size.y - WindowShade.TOP_FRAME);
    }
}
