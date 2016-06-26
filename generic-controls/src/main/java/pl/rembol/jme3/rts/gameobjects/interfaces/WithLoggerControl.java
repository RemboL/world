package pl.rembol.jme3.rts.gameobjects.interfaces;

import pl.rembol.jme3.rts.gameobjects.logger.LoggerControl;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;

import java.util.Optional;

public interface WithLoggerControl extends Selectable {
    default LoggerControl loggerControl() {
        return getNode().getControl(LoggerControl.class);
    }

    default void log(String message) {
        Optional.ofNullable(loggerControl())
                .ifPresent(loggerControl -> loggerControl.addLine(message));
    }
}
