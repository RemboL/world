package pl.rembol.jme3.world.ballman.order;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.MoveTowardsLocationAction;
import pl.rembol.jme3.world.ballman.action.MoveTowardsTargetAction;
import pl.rembol.jme3.world.interfaces.WithNode;

public class MoveOrder extends Order<BallMan> {

    @Override
    protected void doPerform(BallMan ballMan, Vector2f location) {
        ballMan.control().setAction(
                applicationContext.getAutowireCapableBeanFactory()
                        .createBean(MoveTowardsLocationAction.class)
                        .init(ballMan, location, 5f));
    }

    @Override
    protected void doPerform(BallMan ballMan, WithNode target) {
        ballMan.control().setAction(
                applicationContext.getAutowireCapableBeanFactory()
                        .createBean(MoveTowardsTargetAction.class)
                        .init(ballMan, target, 5f));
    }

}
