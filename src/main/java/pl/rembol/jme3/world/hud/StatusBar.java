package pl.rembol.jme3.world.hud;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

public class StatusBar {

	public StatusBar(Node guiNode, AppSettings settings,
			AssetManager assetManager) {

		Picture frame = new Picture("Status Bar");
		frame.setImage(assetManager, "status_bar.png", true);
		frame.move(settings.getWidth() / 2 - 200, 0, -2);
		frame.setWidth(400);
		frame.setHeight(120);
		guiNode.attachChild(frame);

	}

}
