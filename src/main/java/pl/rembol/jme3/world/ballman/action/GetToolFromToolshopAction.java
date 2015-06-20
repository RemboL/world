package pl.rembol.jme3.world.ballman.action;

import java.util.Optional;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.toolshop.Toolshop;
import pl.rembol.jme3.world.hud.ConsoleLog;
import pl.rembol.jme3.world.smallobject.tools.Tool;

public class GetToolFromToolshopAction extends Action {

    private static final float REQUIRED_DISTANCE = 3;
    private Class<? extends Tool> toolClass;
    private boolean finished = false;

    public GetToolFromToolshopAction init(Class<? extends Tool> toolClass) {
        this.toolClass = toolClass;

        return this;
    }

    @Override
    protected boolean start(BallMan ballMan) {
        Optional<Toolshop> closestToolshop = ballMan.getOwner()
                .getClosestToolshop(ballMan.getLocation());

        if (!closestToolshop.isPresent()) {
            applicationContext.getBean(ConsoleLog.class).addLineExternal(
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

            ballMan.addToInventory(toolClass.newInstance().init(
                    applicationContext));
            finished = true;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isFinished(BallMan ballMan) {
        return finished;
    }

}
