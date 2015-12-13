package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.toolshop.Toolshop;
import pl.rembol.jme3.world.smallobject.tools.Tool;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class GetToolFromToolshopAction extends BallManAction {

    private static final float REQUIRED_DISTANCE = 3;

    private Class<? extends Tool> toolClass;

    private boolean finished = false;

    public GetToolFromToolshopAction(GameState gameState, Class<? extends Tool> toolClass) {
        super(gameState);
        this.toolClass = toolClass;
    }

    @Override
    protected boolean start(BallMan ballMan) {
        Optional<Toolshop> closestToolshop = ballManUnitRegistry.getClosestToolshop(ballMan.getLocation(), ballMan.getOwner());

        if (!closestToolshop.isPresent()) {
            gameState.consoleLog.addLineExternal(
                    "Could not find a Toolshop");
            cancel();
            return false;
        }

        if (!assertDistance(ballMan, closestToolshop.get(), REQUIRED_DISTANCE)) {
            return false;
        }

        return true;

    }

    @Override
    protected void doAct(BallMan ballMan, float tpf) {
        try {

            ballMan.addToInventory(toolClass.getDeclaredConstructor(GameState.class).newInstance(gameState));
            finished = true;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException
                e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isFinished(BallMan ballMan) {
        return finished;
    }

}
