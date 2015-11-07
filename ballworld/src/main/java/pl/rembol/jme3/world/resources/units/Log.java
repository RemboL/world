package pl.rembol.jme3.world.resources.units;

import com.jme3.math.Vector3f;
import pl.rembol.jme3.rts.resources.ResourceType;
import pl.rembol.jme3.rts.resources.units.ResourceUnit;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.resources.ResourceTypes;

public class Log extends ResourceUnit {

    public Log(GameState gameState, Vector3f location, int chopCounter) {
        super(gameState, location, chopCounter);
    }

    @Override
    protected String getModelFileName() {
        return "log/log.mesh.xml";
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceTypes.WOOD;
    }

}