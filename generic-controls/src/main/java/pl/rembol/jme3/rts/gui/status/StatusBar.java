package pl.rembol.jme3.rts.gui.status;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import pl.rembol.jme3.rts.events.EventManager;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.rts.gameobjects.selection.SelectionIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatusBar extends Node {

    private static final int ICON_ROW_SIZE = 9;
    private static final int ICON_LINES = 3;
    private List<SelectionIcon> selectionIcons = new ArrayList<>();
    private Node statusDetails = new Node("status details connector");

    private final SimpleApplication simpleApplication;

    public StatusBar(SimpleApplication simpleApplication, AppSettings appSettings, EventManager eventManager) {
        super("status bar");

        this.simpleApplication = simpleApplication;
        simpleApplication.getGuiNode().attachChild(this);
        move(appSettings.getWidth() / 2 - 200, 0, 0);

        attachChild(statusDetails);

        eventManager.onSelectionChanged(selectionChangedEvent -> {
            List<Selectable> selected = selectionChangedEvent.getSelectableList();
            if (selected.size() == 0) {
                clear();
            } else if (selected.size() == 1) {
                Node node = selected.get(0).getStatusDetails();
                if (node != null) {
                    setStatusDetails(node);
                }
            } else {
                setIcons(selected);
            }
        });
    }

    public void initFrame(String themeName) {
        Picture frame = new Picture("Status Bar");
        frame.setImage(simpleApplication.getAssetManager(), "interface/" + themeName + "/status_bar.png", true);
        frame.move(0, 0, -2);
        frame.setWidth(400);
        frame.setHeight(120);
        attachChild(frame);
    }


    public void clearIcons() {
        selectionIcons.forEach(simpleApplication.getGuiNode()::detachChild);

        selectionIcons.clear();
    }

    public void clear() {
        clearIcons();
        statusDetails.detachAllChildren();
    }

    public void setIcons(List<Selectable> selectables) {
        clear();

        int index = 0;
        for (Selectable selectable : selectables.stream()
                .limit(ICON_LINES * ICON_ROW_SIZE).collect(Collectors.toList())) {
            SelectionIcon icon = selectable.getIcon();
            icon.setLocalTranslation(
                    40 + (index % ICON_ROW_SIZE) * 36,
                    40 + (1 - (index / ICON_ROW_SIZE)) * 36,
                    1);
            statusDetails.attachChild(icon);
            selectionIcons.add(icon);

            index++;
        }

    }

    public List<SelectionIcon> getSelectionIcons() {
        return selectionIcons;
    }

    public void setStatusDetails(Node statusDetails) {
        clear();

        this.statusDetails.attachChild(statusDetails);

    }

}
