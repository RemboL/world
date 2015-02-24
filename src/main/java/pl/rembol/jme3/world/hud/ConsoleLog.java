package pl.rembol.jme3.world.hud;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

@Component
public class ConsoleLog {

	private Vector2f framePosition;

	@Autowired
	private Node guiNode;

	@Autowired
	private AppSettings settings;

	@Autowired
	private AssetManager assetManager;

	private Node node;

	@PostConstruct
	public void init() {
		framePosition = new Vector2f(settings.getWidth() / 2 - 200, 150);

		node = new Node("console log");
		node.setLocalTranslation(framePosition.x, framePosition.y, 0);
		guiNode.attachChild(node);
	}

	public void addLine(String text) {
		new ConsoleLogLine(assetManager, node, text);
	}

}
