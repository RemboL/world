package pl.rembol.jme3.world.ballman.order;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.interfaces.WithNode;

public class DefaultActionOrder extends Order<BallMan> {

    public DefaultActionOrder(GameState gameState) {
        super(gameState);
    }

    @Override
    protected void doPerform(BallMan ballMan, Vector2f location) {
        ballMan.control().performDefaultAction(location);
    }

    @Override
    protected void doPerform(BallMan ballMan, WithNode target) {
        ballMan.control().performDefaultAction(target);
    }

}
