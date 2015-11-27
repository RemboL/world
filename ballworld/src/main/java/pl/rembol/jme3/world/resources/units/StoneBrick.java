package pl.rembol.jme3.world.resources.units;

import com.jme3.math.Vector3f;
import pl.rembol.jme3.rts.resources.ResourceType;
import pl.rembol.jme3.rts.resources.units.ResourceUnit;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.world.resources.ResourceTypes;

public class StoneBrick extends ResourceUnit {

    public StoneBrick(GameState gameState, Vector3f location, int chopCounter) {
        super(gameState, location, chopCounter);
    }

    @Override
    protected String getModelFileName() {
        return "stone/stone.mesh.xml";
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceTypes.STONE;
    }

}