package pl.rembol.jme3.world.ballman.order;

import org.springframework.beans.factory.annotation.Autowired;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.MoveTowardsLocationAction;
import pl.rembol.jme3.world.ballman.action.SmoothenTerrainAction;
import pl.rembol.jme3.world.hud.ConsoleLog;
import pl.rembol.jme3.world.interfaces.WithNode;

import com.jme3.math.Vector2f;

public class SmoothenTerrainOrder extends Order<BallMan> {

    @Autowired
    protected ConsoleLog consoleLog;

    @Override
    protected void doPerform(BallMan ballMan, Vector2f location) {
        ballMan.control().setAction(
                applicationContext.getAutowireCapableBeanFactory()
                        .createBean(MoveTowardsLocationAction.class)
                        .init(location, 10f));
        ballMan.control().addAction(
                applicationContext
                        .getAutowireCapableBeanFactory()
                        .createBean(SmoothenTerrainAction.class)
                        .init(location.add(new Vector2f(-5f, -5f)),
                                location.add(new Vector2f(5f, 5f)), 5));
    }

    @Override
    protected void doPerform(BallMan ballMan, WithNode target) {
        consoleLog.addLine("I cannot smoothen the " + target);
    }

}
