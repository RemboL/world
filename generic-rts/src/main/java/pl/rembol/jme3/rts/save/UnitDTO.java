package pl.rembol.jme3.rts.save;

import com.jme3.math.Vector3f;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;

@XStreamAlias("unit")
abstract public class UnitDTO {

    private String key;
    private Vector3f position;

    public UnitDTO(String key, Vector3f position) {
        this.key = key;
        this.position = position;
    }

    public String getKey() {
        return key;
    }

    public Vector3f getPosition() {
        return position;
    }

    abstract public WithNode produce(RtsGameState gameState);

}
