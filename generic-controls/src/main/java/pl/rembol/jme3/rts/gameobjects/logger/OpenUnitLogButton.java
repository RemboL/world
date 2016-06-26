package pl.rembol.jme3.rts.gameobjects.logger;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.unit.Unit;
import pl.rembol.jme3.rts.gui.ClickablePicture;

import java.util.List;
import java.util.stream.Collectors;

public class OpenUnitLogButton extends ClickablePicture {
    public OpenUnitLogButton(GameState gameState) {
        super(gameState);

        picture.setImage(gameState.assetManager, "interface/icons/debug_log.png", true);
        picture.setWidth(32);
        picture.setHeight(32);
    }

    @Override
    public void onClick() {
        List<Unit> selectedUnits = gameState.selectionManager.getSelected().stream()
                .filter(Unit.class::isInstance)
                .map(Unit.class::cast)
                .collect(Collectors.toList());

        if (selectedUnits.size() == 1) {
            gameState.windowManager.addWindowCentered(new LogWindow(gameState, 640, 480, selectedUnits.get(0)));
        }
    }
}
