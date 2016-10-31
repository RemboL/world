package pl.rembol.jme3.world.building;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.resources.Cost;

public abstract class BuildingFactory {

    public Building create(RtsGameState gameState,
                           Vector2f position, boolean startUnderground) {
        return create(gameState).init(position, startUnderground);
    }

    public abstract Building create(RtsGameState gameState);

    public abstract Cost cost();

    public abstract float width();

    public Node createNodeWithScale(RtsGameState gameState) {
        return create(gameState).initNodeWithScale();
    }
}
