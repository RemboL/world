package pl.rembol.jme3.world.player;

import pl.rembol.jme3.world.ModelHelper;
import pl.rembol.jme3.world.interfaces.WithNode;

public interface WithOwner extends WithNode {

    Player getOwner();

    void setOwner(Player player);

    default void updateColor() {
        if (getOwner() != null && getOwner().getColor() != null
                && getNode() != null) {
            for (String geometry : getGeometriesWithChangeableColor()) {
                ModelHelper.setColorToGeometry(getNode(),
                        getOwner().getColor(), geometry);
            }
        }
    }

    String[] getGeometriesWithChangeableColor();

}
