package pl.rembol.jme3.world.interfaces;

import com.jme3.math.Vector3f;

public interface Moving {

    default void lookTowards(WithNode target) {
        lookTowards(target.getNode().getWorldTranslation());
    }

    void lookTowards(Vector3f location);

    void setTargetVelocity(float targetVelocity);

}
