package pl.rembol.jme3.rts.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import pl.rembol.jme3.rts.controls.BlinkControl;
import pl.rembol.jme3.rts.resources.ResourceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ResourcesBar extends Node {

    private Map<ResourceType, Picture> icons = new HashMap<>();

    private Map<ResourceType, BitmapText> texts = new HashMap<>();

    private static final int ICON_SIZE = 32;

    private static final int WIDTH = 500;

    private static final int HEIGHT = 60;

    private static final int ICON_SIZE_PLUS = ICON_SIZE + 8;

    private static final int RESOURCE_OFFSET_SIZE = 115;

    private final SimpleApplication simpleApplication;

    public ResourcesBar(SimpleApplication simpleApplication, AppSettings settings) {
        super("resource bar");
        this.simpleApplication = simpleApplication;

        simpleApplication.getGuiNode().attachChild(this);
        move(settings.getWidth() - WIDTH, settings.getHeight() - HEIGHT, 0);

        Picture frame = new Picture("Resources Box");
        frame.setImage(simpleApplication.getAssetManager(), "interface/resources_bar.png", true);
        frame.move(0, 0, -2);
        frame.setWidth(WIDTH);
        frame.setHeight(HEIGHT);
        attachChild(frame);

    }

    public void init(List<ResourceType> resourceTypeList) {
        BitmapFont guiFont = simpleApplication.getAssetManager()
                .loadFont("Interface/Fonts/Default.fnt");

        resourceTypeList.forEach(resourceType -> initResource(guiFont, resourceType, resourceTypeList.indexOf(resourceType)));
    }

    private void initResource(BitmapFont guiFont, ResourceType type, int offset) {
        Picture icon = new Picture(type.toString() + " icon");
        icon.setImage(simpleApplication.getAssetManager(), "interface/resources/" + type.resourceName() + ".png", true);
        icon.move(30 + RESOURCE_OFFSET_SIZE * offset, 13, -1);
        icon.setWidth(ICON_SIZE);
        icon.setHeight(ICON_SIZE);
        attachChild(icon);

        BitmapText text = new BitmapText(guiFont);
        text.setSize(guiFont.getCharSet().getRenderedSize());
        text.move(30 + RESOURCE_OFFSET_SIZE * offset + ICON_SIZE_PLUS, 16 + text.getLineHeight(),
                0);
        attachChild(text);

        icons.put(type, icon);
        texts.put(type, text);
    }

    private void setResource(Map.Entry<ResourceType, Integer> entry) {
        texts.get(entry.getKey()).setText("" + entry.getValue());
    }

    private void setResource(ResourceType resourceType, int housing, int housingLimit) {
        texts.get(resourceType).setText("" + housing + " / " + housingLimit);
    }

    public void blinkResource(ResourceType type) {
        new BlinkControl(icons.get(type), ICON_SIZE);
    }

    public void updateResources(Map<ResourceType, Integer> resourcesMap, Map<ResourceType, Integer> resourcesLimits) {
        resourcesMap.entrySet().stream()
                .filter(entry -> !entry.getKey().isLimited())
                .forEach(this::setResource);
        resourcesMap.entrySet().stream()
                .filter(entry -> entry.getKey().isLimited())
                .forEach(entry -> setResource(entry.getKey(), entry.getValue(), resourcesLimits.get(entry.getKey())));
    }


}
