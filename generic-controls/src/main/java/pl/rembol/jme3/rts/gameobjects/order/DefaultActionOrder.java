package pl.rembol.jme3.rts.gameobjects.order;

import java.util.List;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithDefaultActionControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;

public class DefaultActionOrder extends Order<WithDefaultActionControl> {

    public DefaultActionOrder(GameState gameState, List<Selectable> selectableList) {
        super(gameState, selectableList);
    }

    @Override
    protected void doPerform(WithDefaultActionControl unit, Vector2f location) {
        unit.defaultActionControl().performDefaultAction(location);
    }

    @Override
    protected void doPerform(WithDefaultActionControl unit, WithNode target) {
        unit.defaultActionControl().performDefaultAction(target);
    }

    @Override
    public boolean isApplicableFor(Selectable unit) {
        return unit instanceof WithDefaultActionControl;
    }

}
