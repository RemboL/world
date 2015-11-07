package pl.rembol.jme3.world;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.pathfinding.Rectangle2f;
import pl.rembol.jme3.world.interfaces.WithNode;

public interface Solid extends WithNode {

    default Rectangle2f getBoundingRectangle(float buffer) {
        return new Rectangle2f(
                new Vector2f(
                        getNode().getWorldTranslation().x,
                        getNode().getWorldTranslation().z),
                new Vector2f(
                        getNode().getWorldTranslation().x,
                        getNode().getWorldTranslation().z))
                .withBuffer(getWidth());
    }

}
