package pl.rembol.jme3.world.resources.units;

import pl.rembol.jme3.world.resources.ResourceType;

public class FruitBasket extends ResourceUnit {

    @Override
    protected String getModelFileName() {
        return "fruitbasket/fruitbasket.mesh.xml";
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.FOOD;
    }

}