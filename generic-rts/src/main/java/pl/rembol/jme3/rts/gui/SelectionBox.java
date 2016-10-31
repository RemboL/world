package pl.rembol.jme3.rts.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

public class SelectionBox extends Node {

    private final SimpleApplication simpleApplication;

    public SelectionBox(SimpleApplication simpleApplication) {
        this.simpleApplication = simpleApplication;

        simpleApplication.getGuiNode().attachChild(this);

    }

    public void initFrame(String themeName) {
        Picture frame = new Picture("Selection Box");
        frame.setImage(simpleApplication.getAssetManager(), "interface/" + themeName + "/selection_box.png", true);
        frame.move(0, 0, -2);
        frame.setWidth(200);
        frame.setHeight(200);
        attachChild(frame);
    }


}
