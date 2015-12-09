package pl.rembol.jme3.rts.gui.window;

import java.util.Optional;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import pl.rembol.jme3.geom.Rectangle2f;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gui.Clickable;

public class Window extends Node {

    protected final GameState gameState;

    private final int width;

    private final int height;

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
        initAddButton();
    }

    protected void initFrame() {
        Picture frame = new Picture("Window frame");
        frame.setImage(gameState.simpleApplication.getAssetManager(),
                "interface/" + gameState.themeName() + "/window_frame.jpg", true);
        frame.move(0, 0, -2);
        frame.setWidth(width);
        frame.setHeight(height);

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

    protected void initAddButton() {
        AddButton addButton = new AddButton(gameState);

        addButton.setLocalTranslation(width - 74, height - 37, 0);
        attachChild(addButton);
    }

    public Integer getTopOffset() {
        return (int) getLocalTranslation().getZ() + 100;
    }

    public void close() {
        gameState.windowManager.closeWindow(this);
    }

    public void click(Vector2f cursorPosition) {
        Optional<Clickable> clicked = getChildren()
                .stream()
                .filter(Clickable.class::isInstance)
                .map(Clickable.class::cast)
                .filter(clickable -> clickable.isClicked(cursorPosition))
                .findFirst();

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
}
