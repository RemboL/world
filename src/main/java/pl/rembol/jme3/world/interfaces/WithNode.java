package pl.rembol.jme3.world.interfaces;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import pl.rembol.jme3.world.save.UnitDTO;

public interface WithNode {

    Node getNode();

    Node initNodeWithScale();

    float getWidth();

    default UnitDTO save(String key) {
        return new UnitDTO(key, getNode().getWorldTranslation());
    }

    default void load(UnitDTO unitDTO) {
    }

    default public Vector3f getLocation() {
        return getNode().getWorldTranslation();
    }

    default public void setAnimation(String animationName, LoopMode loopMode) {
        AnimControl animControl = getNode().getControl(AnimControl.class);
        if (animControl != null) {
            AnimChannel channel = animControl.getChannel(0);
            if (channel != null) {
                channel.setAnim(animationName);
                channel.setLoopMode(loopMode);
            }
        }
    }

}
