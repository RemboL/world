package pl.rembol.jme3.world.hud.status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.rembol.jme3.world.hud.InventoryIcon;
import pl.rembol.jme3.world.hud.InventoryIconCache;
import pl.rembol.jme3.world.input.state.StatusDetails;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.selection.SelectionIcon;
import pl.rembol.jme3.world.smallobject.tools.Tool;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

@Component
public class StatusBar {

    private static final int ICON_ROW_SIZE = 9;
    private static final int ICON_LINES = 3;
    private static final int LINES = 3;
    private List<BitmapText> statusText = new ArrayList<>();
    private List<SelectionIcon> selectionIcons = new ArrayList<>();
    private List<InventoryIcon> inventoryIcons = new ArrayList<>();

    private Vector2f framePosition;

    @Autowired
    private Node guiNode;

    @Autowired
    private AppSettings settings;

    @Autowired
    private AssetManager assetManager;

    @Autowired
    private InventoryIconCache inventoryIconCache;

    @PostConstruct
    public void init() {
        framePosition = new Vector2f(settings.getWidth() / 2 - 200, 0);

        Picture frame = new Picture("Status Bar");
        frame.setImage(assetManager, "interface/status_bar.png", true);
        frame.move(framePosition.x, framePosition.y, -2);
        frame.setWidth(400);
        frame.setHeight(120);
        guiNode.attachChild(frame);

        initText();

    }

    private void initText() {
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

    public void clearText() {
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

    public void setTextAndInventory(List<String> text, List<Tool> tools) {
        clear();

        for (int i = 0; i < text.size() && i < statusText.size(); ++i) {
            statusText.get(i).setText(text.get(i));
        }

        int offset = 0;
        for (Tool tool : tools) {
            InventoryIcon icon = inventoryIconCache.get(tool);
            icon.setLocalTranslation(framePosition.x + 340 - offset,
                    framePosition.y + 14, 1);
            guiNode.attachChild(icon);
            inventoryIcons.add(icon);

            offset += 16;
        }

    }

    public void clearIcons() {
        for (SelectionIcon icon : selectionIcons) {
            guiNode.detachChild(icon);
        }

        selectionIcons.clear();
    }

    public void clearInventory() {
        inventoryIcons.stream().forEach(icon -> guiNode.detachChild(icon));
        inventoryIcons.clear();
    }

    public void clear() {
        clearText();
        clearIcons();
        clearInventory();
    }

    public void setIcons(List<Selectable> selectables) {
        clear();

        int index = 0;
        for (Selectable selectable : selectables.stream()
                .limit(ICON_LINES * ICON_ROW_SIZE).collect(Collectors.toList())) {
            SelectionIcon icon = selectable.getIcon();
            icon.setLocalTranslation(framePosition.x + 40
                    + (index % ICON_ROW_SIZE) * 36, framePosition.y + 44
                    + (1 - (index / ICON_ROW_SIZE)) * 32, 1);
            guiNode.attachChild(icon);
            selectionIcons.add(icon);

            index++;
        }

    }

    public List<SelectionIcon> getSelectionIcons() {
        return selectionIcons;
    }

    public void setStatusDetails(StatusDetails statusDetails) {
        if (statusDetails.inventory().isEmpty()) {
            setText(statusDetails.statusText());
        } else {
            setTextAndInventory(statusDetails.statusText(),
                    statusDetails.inventory());
        }
    }

}
