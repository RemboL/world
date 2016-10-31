package pl.rembol.jme3.world.ballman.hunger;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.EatFoodAction;
import pl.rembol.jme3.world.resources.ResourceTypes;

public class HungerControl extends AbstractControl {

    private RtsGameState gameState;

    private static final int MAX_HUNGER = 50;
    private float hunger = MAX_HUNGER;

    private BallMan ballMan;

    private HungerIndicator hungerIndicator;

    public HungerControl(RtsGameState gameState, BallMan ballMan) {
        this.ballMan = ballMan;
        this.gameState = gameState;
    }

    @Override
    protected void controlRender(RenderManager arg0, ViewPort arg1) {
    }

    @Override
    protected void controlUpdate(float tpf) {
        hunger -= tpf / 10;

        if (ballMan.getOwner().isActive()) {
            updateHungerIndicator(tpf);
        }

        if (isHungry()) {

            if (hasFood()) {
                if (!isBusy()) {
                    eatFood();
                } else if (isVeryHungry()) {
                    eatFoodFast();
                }
            }

        }

        if (hunger < -1) {
            ballMan.strike(1);
            hunger += 1;
        }
    }

    private boolean hasFood() {
        return ballMan.getOwner().getResource(ResourceTypes.FOOD) > 0;
    }

    private void updateHungerIndicator(float tpf) {
        if (hungerIndicator != null && !isHungry()) {
            hungerIndicator.getParent().detachChild(hungerIndicator);
            hungerIndicator = null;
        } else {
            if (hungerIndicator == null && isHungry()) {
                hungerIndicator = new HungerIndicator(gameState, ballMan, hungerFactor());
            }
        }

        if (hungerIndicator != null) {
            hungerIndicator.hunger(hungerFactor());

            hungerIndicator.rotate(new Quaternion().fromAngleAxis(tpf,
                    Vector3f.UNIT_Y));
        }
    }

    private float hungerFactor() {
        return hunger / MAX_HUNGER;
    }

    private void eatFood() {
        if (!ballMan.control().contains(EatFoodAction.class)) {
            ballMan.control().addAction(new EatFoodAction(gameState, MAX_HUNGER - hunger));
        }
    }

    private void eatFoodFast() {
        if (!ballMan.control().startsWith(EatFoodAction.class)) {
            ballMan.control().addActionOnStart(new EatFoodAction(gameState, MAX_HUNGER - hunger));
        }
    }

    private boolean isBusy() {
        return !ballMan.control().isEmpty();
    }

    private boolean isHungry() {
        return hungerFactor() < .2;
    }

    private boolean isVeryHungry() {
        return hungerFactor() < .05;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial == null) {
            destoy();
        }
    }

    private void destoy() {
        if (hungerIndicator != null) {
            hungerIndicator.getParent().detachChild(hungerIndicator);
        }

    }

    public void eat(int food) {
        hunger += food;
    }

}
