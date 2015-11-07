package pl.rembol.jme3.world.resources.units;

import com.jme3.math.Vector3f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.resources.ResourceType;

public class FruitBasket extends ResourceUnit {

    public FruitBasket(GameState gameState, Vector3f location, int chopCounter) {
        super(gameState, location, chopCounter);
    }

    @Override
    protected String getModelFileName() {
        return "fruitbasket/fruitbasket.mesh.xml";
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.FOOD;
    }

}