package pl.rembol.jme3.world.resources.units;

import pl.rembol.jme3.world.resources.ResourceType;

public class StoneBrick extends ResourceUnit {

    @Override
    protected String getModelFileName() {
        return "stone/stone.mesh.xml";
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.STONE;
    }

}