package pl.rembol.jme3.rts.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.ui.Picture;

public class SelectionBox {

    public SelectionBox(SimpleApplication simpleApplication) {
        Picture frame = new Picture("Selection Box");
        frame.setImage(simpleApplication.getAssetManager(), "interface/selection_box.png", true);
        frame.move(0, 0, -2);
        frame.setWidth(200);
        frame.setHeight(200);
        simpleApplication.getGuiNode().attachChild(frame);

    }
}
