package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.interfaces.WithNode;

import com.jme3.math.Vector2f;

public class DefaultActionOrder extends Order<BallMan> {

    @Override
    protected void doPerform(BallMan ballMan, Vector2f location) {
        ballMan.control().performDefaultAction(location);
    }

    @Override
    protected void doPerform(BallMan ballMan, WithNode target) {
        ballMan.control().performDefaultAction(target);
    }

}
