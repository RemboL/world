package pl.rembol.jme3.world.hud;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.rembol.jme3.input.state.Command;
import pl.rembol.jme3.input.state.InputStateManager;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

@Component
public class ActionBox {

	private Picture frame;
	private Node buttonsNode;

	@Autowired
	private Node guiNode;

	@Autowired
	private AppSettings settings;

	@Autowired
	private AssetManager assetManager;

	@Autowired
	private InputStateManager inputStateManager;

	@PostConstruct
	public void init() {

		frame = new Picture("Action Box");
		frame.setImage(assetManager, "interface/action_box.png", true);
		frame.move(settings.getWidth() - 200, 0, -2);
		frame.setWidth(200);
		frame.setHeight(200);
		guiNode.attachChild(frame);

		buttonsNode = new Node("Action buttons");
		buttonsNode.move(settings.getWidth() - 172, 146, 0);
		guiNode.attachChild(buttonsNode);

	}

	public void updateActionButtons() {
		clearButtons();
		List<Command> availableCommands = inputStateManager
				.getAvailableCommands();
		for (Command command : availableCommands) {
			createActionButton(command);
		}
	}

	private void createActionButton(Command command) {
		Picture button = new ActionButton(command, assetManager);
		buttonsNode.attachChild(button);
	}

	private void clearButtons() {
		buttonsNode.detachAllChildren();
	}

	public Node getActionButtonNode() {
		return buttonsNode;
	}

}
