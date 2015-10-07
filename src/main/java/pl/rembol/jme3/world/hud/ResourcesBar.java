package pl.rembol.jme3.world.hud;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.rembol.jme3.world.resources.ResourceType;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static pl.rembol.jme3.world.resources.ResourceType.*;

@Component
public class ResourcesBar {
    private Picture frame;

    private Map<ResourceType, Picture> icons = new HashMap<>();
    private Map<ResourceType, BitmapText> texts = new HashMap<>();

    private static final int ICON_SIZE = 32;

    private static final int WIDTH = 500;

    private static final int HEIGHT = 60;

    private static final int ICON_SIZE_PLUS = ICON_SIZE + 8;

    private static final int RESOURCE_OFFSET_SIZE = 115;

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
        framePosition = new Vector2f(settings.getWidth() - WIDTH,
                settings.getHeight() - HEIGHT);
        frame.move(framePosition.x, framePosition.y, -2);
        frame.setWidth(WIDTH);
        frame.setHeight(HEIGHT);
        guiNode.attachChild(frame);

        initIconsAndTexts(guiNode, settings, assetManager);

    }

    private void initIconsAndTexts(Node guiNode, AppSettings settings,
            AssetManager assetManager) {
        BitmapFont guiFont = assetManager
                .loadFont("Interface/Fonts/Default.fnt");

        initResource(guiNode, assetManager, guiFont, FOOD, 0);
        initResource(guiNode, assetManager, guiFont, WOOD, 1);
        initResource(guiNode, assetManager, guiFont, STONE, 2);
        initResource(guiNode, assetManager, guiFont, HOUSING, 3);

    }

    private void initResource(Node guiNode, AssetManager assetManager,
            BitmapFont guiFont, ResourceType type, int offset) {
        Picture icon = new Picture(type.toString() + " icon");
        icon.setImage(assetManager,
                "interface/resources/" + type.resourceName() + ".png", true);
        icon.move(framePosition.x + 40 + RESOURCE_OFFSET_SIZE * offset,
                framePosition.y + 13, -1);
        icon.setWidth(ICON_SIZE);
        icon.setHeight(ICON_SIZE);
        guiNode.attachChild(icon);

        BitmapText text = new BitmapText(guiFont);
        text.setSize(guiFont.getCharSet().getRenderedSize());
        text.move(framePosition.x + 40 + RESOURCE_OFFSET_SIZE * offset
                + ICON_SIZE_PLUS, framePosition.y + 16 + text.getLineHeight(),
                0);
        guiNode.attachChild(text);

        icons.put(type, icon);
        texts.put(type, text);
    }

    private void setResource(Map.Entry<ResourceType, Integer> entry) {
        texts.get(entry.getKey()).setText("" + entry.getValue());
    }

    private void setHousing(int housing, int housingLimit) {
        texts.get(HOUSING).setText("" + housing + " / " + housingLimit);
    }

    public void blinkResource(ResourceType type) {
        new BlinkControl(icons.get(type), ICON_SIZE);
    }

    public void updateResources(Map<ResourceType, Integer> resourcesMap,
            int housingLimit) {
        resourcesMap.entrySet().stream()
                .filter(entry -> entry.getKey() != HOUSING)
                .forEach(this::setResource);
        setHousing(resourcesMap.get(HOUSING), housingLimit);
    }

}
