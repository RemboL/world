package pl.rembol.jme3.rts.gui.window;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.jme3.ui.Picture;
import pl.rembol.jme3.geom.Rectangle2f;
import pl.rembol.jme3.geom.Vector2i;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gui.Clickable;
import pl.rembol.jme3.utils.Spatials;

import java.util.Optional;

public class Window extends Node {

    private static final float WINDOW_FRAME_WIDTH = 800;

    private static final float WINDOW_FRAME_HEIGHT = 600;

    protected final GameState gameState;

    protected final int width;

    protected final int height;

    protected WindowShade windowShade;

    public Window(GameState gameState, int width, int height) {
        this.gameState = gameState;
        this.width = width;
        this.height = height;

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
        frame.setWidth(width);
        frame.setHeight(height);

        Quad quad = new Quad(1.0F, 1.0F, false);
        float textureBoundX = Math.min(width / WINDOW_FRAME_WIDTH, 1);
        float textureBoundY = Math.min(height / WINDOW_FRAME_HEIGHT, 1);
        quad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{0.0F, 0.0F, textureBoundX, 0.0F, textureBoundX, textureBoundY, 0.0F, textureBoundY});
        frame.setMesh(quad);


        attachChild(frame);
    }

    protected void initShades() {
        windowShade.initAllShades();
    }

    protected void initCloseButton() {
        CloseButton closeButton = new CloseButton(gameState, this);

        closeButton.setLocalTranslation(width - 37, height - 37, 0);
        attachChild(closeButton);
    }

    public Integer getTopOffset() {
        return (int) getLocalTranslation().getZ() + 100;
    }

    public void close() {
        gameState.windowManager.closeWindow(this);
    }

    private Optional<Clickable> findClickedChild(Vector2f cursorPosition) {
        return Spatials.getDescendants(this)
                .filter(Clickable.class::isInstance)
                .map(Clickable.class::cast)
                .filter(clickable -> clickable.isClicked(cursorPosition))
                .findFirst();
    }

    public void click(Vector2f cursorPosition) {
        Optional<Clickable> clicked = findClickedChild(cursorPosition);

        if (clicked.isPresent()) {
            clicked.get().onClick();
        } else {
            if (!new Rectangle2f(
                    new Vector2f(getLocalTranslation().x, getLocalTranslation().y),
                    new Vector2f(getLocalTranslation().x + width, getLocalTranslation().y + height))
                    .isInside(cursorPosition)) {
                onClickOutside();
            }
        }
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
}
