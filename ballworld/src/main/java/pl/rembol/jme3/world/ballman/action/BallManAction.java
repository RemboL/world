package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.rts.unit.action.Action;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.smallobject.tools.Tool;

import java.util.Optional;

public abstract class BallManAction extends Action<BallMan> {

    public BallManAction(GameState gameState) {
        super(gameState);
    }

    protected boolean assertWielded(BallMan ballMan,
                                    Optional<Class<? extends Tool>> wieldedClass) {
        if (!wieldedClass.isPresent()) {
            if (ballMan.getWieldedObject(BallMan.Hand.RIGHT) != null) {
                ballMan.control().addActionOnStart(
                        new SwitchToolAction(gameState, Optional.empty()).withParent(this));
                return false;
            }
            return true;
        }

        if (!wieldedClass.get()
                .isInstance(ballMan.getWieldedObject(BallMan.Hand.RIGHT))) {
            try {
                Optional<Tool> toolFromInventory = ballMan.inventory().get(
                        wieldedClass.get());
                if (toolFromInventory.isPresent()) {
                    ballMan.control().addActionOnStart(
                            new SwitchToolAction(gameState, toolFromInventory).withParent(
                                    this));
                    return false;
                } else {
                    ballMan.control().addActionOnStart(
                            new GetToolFromToolshopAction(gameState, wieldedClass.get())
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
