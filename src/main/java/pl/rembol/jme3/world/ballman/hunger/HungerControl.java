package pl.rembol.jme3.world.ballman.hunger;

import org.springframework.context.ApplicationContext;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.EatFoodAction;
import pl.rembol.jme3.world.resources.ResourceType;

public class HungerControl extends AbstractControl {
    private ApplicationContext applicationContext;

    private static final int MAX_HUNGER = 50;
    private float hunger = MAX_HUNGER * FastMath.nextRandomFloat();

    private BallMan ballMan;

    private HungerIndicator hungerIndicator;

    public HungerControl(ApplicationContext applicationContext, BallMan ballMan) {
        this.ballMan = ballMan;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void controlRender(RenderManager arg0, ViewPort arg1) {
    }

    @Override
    protected void controlUpdate(float tpf) {
        hunger -= tpf / 5;

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
        return ballMan.getOwner().getResource(ResourceType.FOOD) > 0;
    }

    private void updateHungerIndicator(float tpf) {
        if (hungerIndicator != null && !isHungry()) {
            hungerIndicator.getParent().detachChild(hungerIndicator);
            hungerIndicator = null;
        } else {
            if (hungerIndicator == null && isHungry()) {
                hungerIndicator = new HungerIndicator().init(
                        applicationContext.getBean(GameState.class), ballMan, hungerFactor());
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
            ballMan.control().addAction(
                    applicationContext.getAutowireCapableBeanFactory()
                            .createBean(EatFoodAction.class)
                            .init(MAX_HUNGER - hunger));
        }
    }

    private void eatFoodFast() {
        if (!ballMan.control().startsWith(EatFoodAction.class)) {
            ballMan.control().addActionOnStart(
                    applicationContext.getAutowireCapableBeanFactory()
                            .createBean(EatFoodAction.class)
                            .init(MAX_HUNGER - hunger));
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
