package pl.rembol.jme3.rts.gameobjects.order;

import com.jme3.scene.Node;

public interface WithSilhouette {
    Node createNode();

    default float requiredFreeWidth() {
        return -1f;
    }

    default boolean snapTargetPositionToGrid() { return false; }
}
