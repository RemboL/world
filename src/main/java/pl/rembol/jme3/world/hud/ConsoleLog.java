package pl.rembol.jme3.world.hud;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import pl.rembol.jme3.world.GameState;

public class ConsoleLog {

    private GameState gameState;

    private Node node;

    public ConsoleLog(GameState gameState) {
        this.gameState = gameState;

        Vector2f framePosition = new Vector2f(gameState.settings.getWidth() / 2 - 200, 150);

        node = new Node("console log");
        node.setLocalTranslation(framePosition.x, framePosition.y, 0);
        gameState.guiNode.attachChild(node);
    }

    public void addLine(String text) {
        new ConsoleLogLine(gameState, node, text);
    }

    public void addLineExternal(String text) {
        gameState.simpleApplication.enqueue(() -> {
            addLine(text);
            return null;
        });
    }
}
