package pl.rembol.jme3.world.hud;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

@Component
public class ResourcesBar {
	private Picture frame;
	private Picture woodIcon;
	private BitmapText woodText;
    private Picture stoneIcon;
    private BitmapText stoneText;
	private Picture housingIcon;
	private BitmapText housingText;

	private static final float ICON_SIZE = 32;

	private Vector2f framePosition;

	@Autowired
	private Node guiNode;

	@Autowired
	private AssetManager assetManager;

	@Autowired
	private AppSettings settings;

	@PostConstruct
	public void init() {

		frame = new Picture("Resources Box");
		frame.setImage(assetManager, "interface/resources_bar.png", true);
		framePosition = new Vector2f(settings.getWidth() - 400,
				settings.getHeight() - 60);
		frame.move(framePosition.x, framePosition.y, -2);
		frame.setWidth(400);
		frame.setHeight(60);
		guiNode.attachChild(frame);

		initIconsAndTexts(guiNode, settings, assetManager);

	}

	private void initIconsAndTexts(Node guiNode, AppSettings settings,
			AssetManager assetManager) {
		BitmapFont guiFont = assetManager
				.loadFont("Interface/Fonts/Default.fnt");

		woodIcon = new Picture("Wood icon");
		woodIcon.setImage(assetManager, "interface/resources/wood.png", true);
		woodIcon.move(framePosition.x + 25, framePosition.y + 13, -1);
		woodIcon.setWidth(ICON_SIZE);
		woodIcon.setHeight(ICON_SIZE);
		guiNode.attachChild(woodIcon);

		woodText = new BitmapText(guiFont);
		woodText.setSize(guiFont.getCharSet().getRenderedSize());
		woodText.move(framePosition.x + 65,
				framePosition.y + 16 + woodText.getLineHeight(), 0);
		guiNode.attachChild(woodText);
		
		stoneIcon = new Picture("Stone icon");
		stoneIcon.setImage(assetManager, "interface/resources/stone.png", true);
		stoneIcon.move(framePosition.x + 125, framePosition.y + 13, -1);
		stoneIcon.setWidth(ICON_SIZE);
		stoneIcon.setHeight(ICON_SIZE);
        guiNode.attachChild(stoneIcon);

        stoneText = new BitmapText(guiFont);
        stoneText.setSize(guiFont.getCharSet().getRenderedSize());
        stoneText.move(framePosition.x + 165,
                framePosition.y + 16 + woodText.getLineHeight(), 0);
        guiNode.attachChild(stoneText);

		housingIcon = new Picture("Housing icon");
		housingIcon.setImage(assetManager, "interface/resources/housing.png",
				true);
		housingIcon.move(framePosition.x + 225, framePosition.y + 13, -1);
		housingIcon.setWidth(ICON_SIZE);
		housingIcon.setHeight(ICON_SIZE);
		guiNode.attachChild(housingIcon);

		housingText = new BitmapText(guiFont);
		housingText.setSize(guiFont.getCharSet().getRenderedSize());
		housingText.move(framePosition.x + 265,
				framePosition.y + 16 + woodText.getLineHeight(), 0);
		guiNode.attachChild(housingText);

	}

	public void setWood(int wood) {
		woodText.setText("" + wood);
	}
	
	public void setStone(int stone) {
        stoneText.setText("" + stone);
    }

	public void setHousing(int housing, int housingLimit) {
		housingText.setText("" + housing + " / " + housingLimit);
	}

	public void blinkWood() {
		new BlinkControl(woodIcon, ICON_SIZE);
	}

    public void blinkStone() {
        new BlinkControl(stoneIcon, ICON_SIZE);
    }
    
	public void blinkHousing() {
		new BlinkControl(housingIcon, ICON_SIZE);
	}

	public void updateResources(int wood, int stone, int housing, int housingLimit) {
		setWood(wood);
		setStone(stone);
		setHousing(housing, housingLimit);
	}
}
