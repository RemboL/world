package pl.rembol.jme3.rts.unit.order;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.unit.interfaces.WithNode;
import pl.rembol.jme3.rts.unit.selection.Selectable;

public abstract class Order<Type extends Selectable> {

    protected List<Type> selected = new ArrayList<>();

    protected GameState gameState;

    public Order(GameState gameState, List<Selectable> selected) {

        this.gameState = gameState;
        if (selected != null) {
            this.selected = selected.stream()
                    .filter(this::isApplicableFor)
                    .map(selectable -> (Type) selectable)
                    .collect(Collectors.toList());
        }
    }

    public void perform(Vector2f location) {
        for (Type ballMan : selected) {
            doPerform(ballMan, location);
        }
    }

    public void perform(WithNode target) {
        for (Type ballMan : selected) {
            doPerform(ballMan, target);
        }
    }

    protected abstract void doPerform(Type unit, Vector2f location);

    protected abstract void doPerform(Type unit, WithNode target);

    public abstract boolean isApplicableFor(Selectable unit);

}
