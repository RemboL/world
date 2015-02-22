package pl.rembol.jme3.world.hud;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

@Component
public class SelectionBox {

	private Picture frame;

	@Autowired
	private Node guiNode;

	@Autowired
	private AssetManager assetManager;

	@PostConstruct
	public void init() {

		frame = new Picture("Selection Box");
		frame.setImage(assetManager, "interface/selection_box.png", true);
		frame.move(0, 0, -2);
		frame.setWidth(200);
		frame.setHeight(200);
		guiNode.attachChild(frame);

	}
}
