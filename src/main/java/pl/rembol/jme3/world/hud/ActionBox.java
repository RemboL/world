package pl.rembol.jme3.world.hud;

import java.util.List;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.input.state.Command;
import pl.rembol.jme3.world.input.state.InputStateManager;

@Component
public class ActionBox {

	private Picture frame;
	private Node buttonsNode;

	@Autowired
	private GameState gameState;

	@Autowired
	private InputStateManager inputStateManager;

	@PostConstruct
	public void init() {

		frame = new Picture("Action Box");
		frame.setImage(gameState.assetManager, "interface/action_box.png", true);
		frame.move(gameState.settings.getWidth() - 200, 0, -2);
		frame.setWidth(200);
		frame.setHeight(200);
		gameState.guiNode.attachChild(frame);

		buttonsNode = new Node("Action buttons");
		buttonsNode.move(gameState.settings.getWidth() - 172, 146, 0);
		gameState.guiNode.attachChild(buttonsNode);

	}

	public void updateActionButtons() {
		clearButtons();
		List<Command> availableCommands = inputStateManager
				.getAvailableCommands();
		availableCommands.forEach(this::createActionButton);
	}

	private void createActionButton(Command command) {
		Picture button = new ActionButton(command, gameState);
		buttonsNode.attachChild(button);
	}

	private void clearButtons() {
		buttonsNode.detachAllChildren();
	}

	public Node getActionButtonNode() {
		return buttonsNode;
	}

}
