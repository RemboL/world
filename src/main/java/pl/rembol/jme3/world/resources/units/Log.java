package pl.rembol.jme3.world.resources.units;

import pl.rembol.jme3.world.resources.ResourceType;

public class Log extends ResourceUnit {

    @Override
    protected String getModelFileName() {
        return "log/log.mesh.xml";
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.WOOD;
    }

}