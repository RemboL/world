package pl.rembol.jme3.rts.gui.window;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gui.Clickable;

public class Window extends Node {

    protected final GameState gameState;

    private int width = 640;
    private int height = 480;

    public Window(GameState gameState) {
        this.gameState = gameState;

        initFrame();

        initShades();

        initCloseButton();
        initAddButton();
    }

    private void initFrame() {
        Picture frame = new Picture("Window frame");
        frame.setImage(gameState.simpleApplication.getAssetManager(), "interface/" + gameState.themeName() + "/window_frame.jpg", true);
        frame.move(0, 0, -2);
        frame.setWidth(width);
        frame.setHeight(height);

        attachChild(frame);
    }

    private void initShades() {
        attachChild(new WindowShade(gameState, width, height, -1));
    }

    private void initCloseButton() {
        CloseButton closeButton = new CloseButton(gameState, this);

        closeButton.setLocalTranslation(width - 37, height - 37, 0);
        attachChild(closeButton);
    }

    private void initAddButton() {
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
        getChildren()
                .stream()
                .filter(Clickable.class::isInstance)
                .map(Clickable.class::cast)
                .filter(clickable -> clickable.isClicked(cursorPosition))
                .findFirst()
                .ifPresent(Clickable::onClick);
    }
}
