package pl.rembol.jme3.rts.gui.window;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.GameState;

public class WindowManager {

    private static final Integer BOTTOM_OFFSET = 100;
    private final List<Window> windowStack = new ArrayList<>();

    private final GameState gameState;

    public WindowManager(GameState gameState) {
        this.gameState = gameState;
    }

    public void addWindow(Window window, Vector2f location) {
        gameState.guiNode.attachChild(window);

        location = normalizeWindowLocation(window, location);

        window.setLocalTranslation(location.x, location.y, getTopWindowOffset());
        windowStack.add(window);
    }

    private Vector2f normalizeWindowLocation(Window window, Vector2f location) {
        Vector2f newLocation = location.clone();
        if (newLocation.x < 0) {
            newLocation.x = 0;
        }

        if (newLocation.y < 0) {
            newLocation.y = 0;
        }
        if (newLocation.x + window.width > gameState.settings.getWidth()) {
            newLocation.x = gameState.settings.getWidth() - window.width;
        }
        if (newLocation.y + window.height > gameState.settings.getHeight()) {
            newLocation.y = gameState.settings.getHeight() - window.height;
        }
        return newLocation;
    }

    public void closeWindow(Window window) {
        gameState.guiNode.detachChild(window);
        windowStack.remove(window);
    }

    private int getTopWindowOffset() {
        return windowStack
                .stream()
                .map(Window::getTopOffset)
                .reduce(Math::max)
                .orElse(BOTTOM_OFFSET);
    }

    public Optional<Window> getTopWindow() {
        return windowStack
                .stream()
                .sorted((o1, o2) -> -Integer.compare(o1.getTopOffset(), o2.getTopOffset()))
                .findFirst();
    }

}
