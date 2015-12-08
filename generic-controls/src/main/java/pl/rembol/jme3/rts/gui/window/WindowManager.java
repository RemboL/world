package pl.rembol.jme3.rts.gui.window;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WindowManager {

    private static final Integer BOTTOM_OFFSET = 100;
    private final List<Window> windowStack = new ArrayList<>();

    private final GameState gameState;

    public WindowManager(GameState gameState) {
        this.gameState = gameState;
    }

    public void addWindow(Window window, Vector2f location) {
        gameState.guiNode.attachChild(window);
        window.setLocalTranslation(location.x, location.y, getTopWindowOffset());
        windowStack.add(window);
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
