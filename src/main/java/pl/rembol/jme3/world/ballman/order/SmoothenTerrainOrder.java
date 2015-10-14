package pl.rembol.jme3.world.ballman.order;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.SmoothenTerrainAction;
import pl.rembol.jme3.world.interfaces.WithNode;

public class SmoothenTerrainOrder extends Order<BallMan> {

    @Override
    protected void doPerform(BallMan ballMan, Vector2f location) {
        ballMan.control().addAction(
                new SmoothenTerrainAction(applicationContext.getBean(GameState.class),
                        location.add(new Vector2f(-5f, -5f)),
                        location.add(new Vector2f(5f, 5f)), 5));
    }

    @Override
    protected void doPerform(BallMan ballMan, WithNode target) {
        applicationContext.getBean(GameState.class).consoleLog.addLine("I cannot smoothen the " + target);
    }

}
