package pl.rembol.jme3.world.ballman.order;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.selection.Selectable;

import java.util.ArrayList;
import java.util.List;

public abstract class Order<OrderedType extends Selectable> {

    protected List<OrderedType> selected = new ArrayList<>();

    protected GameState gameState;

    public Order(GameState gameState) {
        this.gameState = gameState;
    }

    public void perform(Vector2f location) {
        for (OrderedType ballMan : selected) {
            doPerform(ballMan, location);
        }
    }

    public void perform(WithNode target) {
        for (OrderedType ballMan : selected) {
            doPerform(ballMan, target);
        }
    }

    protected abstract void doPerform(OrderedType ballMan, Vector2f location);

    protected abstract void doPerform(OrderedType ballMan, WithNode target);

    public void setSelected(List<OrderedType> selected) {
        this.selected = selected;
    }

}
