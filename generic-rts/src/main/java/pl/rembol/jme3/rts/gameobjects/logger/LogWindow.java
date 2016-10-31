package pl.rembol.jme3.rts.gameobjects.logger;

import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.unit.Unit;
import pl.rembol.jme3.rts.gui.window.text.TextWindow;

public class LogWindow extends TextWindow {
    public LogWindow(RtsGameState gameState, int width, int height, Unit unit) {
        super(gameState, width, height);

        unit.loggerControl().getLog().forEach(this::addText);
    }
}
