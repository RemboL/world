package pl.rembol.jme3.rts.gui.console;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

public class ConsoleLog extends Node {

    private SimpleApplication simpleApplication;

    public ConsoleLog(SimpleApplication simpleApplication, AppSettings appSettings) {
        super("console log");

        this.simpleApplication = simpleApplication;

        Vector2f framePosition = new Vector2f(appSettings.getWidth() / 2 - 200, 150);

        setLocalTranslation(framePosition.x, framePosition.y, 0);
        simpleApplication.getGuiNode().attachChild(this);
    }

    public void addLine(String text) {
        new ConsoleLogLine(simpleApplication.getAssetManager(), this, text);
    }

    public void addLineExternal(String text) {
        simpleApplication.enqueue(() -> addLine(text));
    }
}
