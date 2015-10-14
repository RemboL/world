package pl.rembol.jme3.world.building;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.resources.Cost;

public abstract class BuildingFactory {

    public Building create(GameState gameState,
                           Vector2f position, boolean startUnderground) {
        return create(gameState).init(position, startUnderground);
    }

    public abstract Building create(GameState gameState);

    public abstract Cost cost();

    public abstract float width();

    public Node createNodeWithScale(GameState gameState) {
        return create(gameState).initNodeWithScale();
    }
}
