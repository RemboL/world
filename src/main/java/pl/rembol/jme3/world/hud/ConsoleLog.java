package pl.rembol.jme3.world.hud;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import pl.rembol.jme3.world.GameState;

@Component
public class ConsoleLog {

	private Vector2f framePosition;

	@Autowired
	private GameState gameState;

	private Node node;

	@PostConstruct
	public void init() {
		framePosition = new Vector2f(gameState.settings.getWidth() / 2 - 200, 150);

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
