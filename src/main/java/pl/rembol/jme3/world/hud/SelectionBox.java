package pl.rembol.jme3.world.hud;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jme3.ui.Picture;
import pl.rembol.jme3.world.GameState;

@Component
public class SelectionBox {

	private Picture frame;

	@Autowired
	private GameState gameState;

	@PostConstruct
	public void init() {

		frame = new Picture("Selection Box");
		frame.setImage(gameState.assetManager, "interface/selection_box.png", true);
		frame.move(0, 0, -2);
		frame.setWidth(200);
		frame.setHeight(200);
		gameState.guiNode.attachChild(frame);

	}
}
