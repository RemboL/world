package pl.rembol.jme3.world.hud;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

public class SelectionBox {

	private BitmapText distanceText;
	private Picture frame;

	public SelectionBox(Node guiNode, AppSettings settings,
			AssetManager assetManager) {

		frame = new Picture("Selection Box");
		frame.setImage(assetManager, "interface/selection_box.png", true);
		frame.move(0, 0, -2);
		frame.setWidth(200);
		frame.setHeight(200);
		guiNode.attachChild(frame);

		initText(guiNode, settings, assetManager);

	}

	private void initText(Node guiNode, AppSettings settings,
			AssetManager assetManager) {
		BitmapFont guiFont = assetManager
				.loadFont("Interface/Fonts/Default.fnt");
		distanceText = new BitmapText(guiFont);
		distanceText.setSize(guiFont.getCharSet().getRenderedSize());
		guiNode.attachChild(distanceText);
		distanceText.move(50, 150 + distanceText.getLineHeight(), 0);
	}

	public void setText(String text) {
		distanceText.setText(text);
	}

}
