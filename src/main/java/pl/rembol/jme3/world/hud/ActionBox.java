package pl.rembol.jme3.world.hud;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

public class ActionBox {

	public ActionBox(Node guiNode, AppSettings settings,
			AssetManager assetManager) {

		Picture frame = new Picture("Action Box");
		frame.setImage(assetManager, "action_box.png", true);
		frame.move(settings.getWidth() - 200, 0, -2);
		frame.setWidth(200);
		frame.setHeight(200);
		guiNode.attachChild(frame);

	}

}
