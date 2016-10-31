package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.action.Action;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballmanunitregistry.BallManUnitRegistry;
import pl.rembol.jme3.world.smallobject.tools.Tool;

import java.util.Optional;

abstract class BallManAction extends Action<BallMan> {

    BallManUnitRegistry ballManUnitRegistry;

    BallManAction(RtsGameState gameState) {
        super(gameState);
        ballManUnitRegistry = new BallManUnitRegistry(gameState);
    }

    boolean assertWielded(BallMan ballMan,
                          Class<? extends Tool> wieldedClass) {
        if (wieldedClass == null) {
            if (ballMan.getWieldedObject(BallMan.Hand.RIGHT) != null) {
                ballMan.control().addActionOnStart(
                        new SwitchToolAction(gameState, null).withParent(this));
                return false;
            }
            return true;
        }

        if (!wieldedClass.isInstance(ballMan.getWieldedObject(BallMan.Hand.RIGHT))) {
            try {
                Optional<Tool> toolFromInventory = ballMan.inventory().get(
                        wieldedClass);
                if (toolFromInventory.isPresent()) {
                    ballMan.control().addActionOnStart(
                            new SwitchToolAction(gameState, toolFromInventory.get()).withParent(
                                    this));
                    return false;
                } else {
                    ballMan.control().addActionOnStart(
                            new GetToolFromToolshopAction(gameState, wieldedClass)
                                    .withParent(this));
                    return false;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            return false;
        }

        return true;
    }
}
