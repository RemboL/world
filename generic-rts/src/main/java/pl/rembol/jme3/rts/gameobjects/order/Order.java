package pl.rembol.jme3.rts.gameobjects.order;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Order<Type extends Selectable> {

    protected List<Type> selected = new ArrayList<>();

    protected RtsGameState gameState;

    public Order(RtsGameState gameState, List<Selectable> selected) {

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
