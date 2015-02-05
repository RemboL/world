package pl.rembol.jme3.world.hud;

import java.util.ArrayList;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

public class StatusBar {

	private static final int LINES = 3;
	private List<BitmapText> statusText = new ArrayList<>();

	private Vector2f framePosition;

	public StatusBar(Node guiNode, AppSettings settings,
			AssetManager assetManager) {
		framePosition = new Vector2f(settings.getWidth() / 2 - 200, 0);

		Picture frame = new Picture("Status Bar");
		frame.setImage(assetManager, "status_bar.png", true);
		frame.move(framePosition.x, framePosition.y, -2);
		frame.setWidth(400);
		frame.setHeight(120);
		guiNode.attachChild(frame);

		initText(guiNode, settings, assetManager);

	}

	private void initText(Node guiNode, AppSettings settings,
			AssetManager assetManager) {
		BitmapFont guiFont = assetManager
				.loadFont("Interface/Fonts/Default.fnt");

		for (int i = 0; i < LINES; ++i) {
			BitmapText textLine = new BitmapText(guiFont);
			textLine.setSize(guiFont.getCharSet().getRenderedSize());
			guiNode.attachChild(textLine);
			textLine.move(framePosition.x + 50, framePosition.y + 80 + (1 - i)
					* textLine.getLineHeight(), 0);
			statusText.add(textLine);
		}
	}

	public void clear() {
		statusText.stream().forEach(text -> text.setText(""));
	}

	public void setText(String text) {
		clear();

		statusText.get(0).setText(text);
	}

	public void setText(List<String> text) {
		clear();

		for (int i = 0; i < text.size() && i < statusText.size(); ++i) {
			statusText.get(i).setText(text.get(i));
		}
	}

}
