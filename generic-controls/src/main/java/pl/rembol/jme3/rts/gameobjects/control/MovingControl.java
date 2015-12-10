package pl.rembol.jme3.rts.gameobjects.control;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;

public class MovingControl extends AbstractControl {

    private Vector3f targetDirection;

    private float targetVelocity = 0;

    private float maxVelocity = 5f;

    private float acceleration = .1f;

    private boolean rotationOnlyOnMove = false;

    private WithNode unit;

    public MovingControl(WithNode unit) {
        this.unit = unit;
    }

    @Override
    protected void controlRender(RenderManager arg0, ViewPort arg1) {
    }

    @Override
    protected void controlUpdate(float tpf) {
        BetterCharacterControl characterControl = getCharacterControl();

        if (characterControl != null) {
            if (targetDirection != null) {
                characterControl.setViewDirection(calculateNewDirection(characterControl));
            }

            characterControl.setWalkDirection(
                    characterControl
                            .getViewDirection()
                            .mult(calculateNewVelocity(characterControl.getWalkDirection().length())));
        }
    }

    private Vector3f calculateNewDirection(BetterCharacterControl characterControl) {
        Vector3f currentDirection = characterControl.getViewDirection();
        if (!rotationOnlyOnMove) {
            return currentDirection.add(targetDirection).setY(0).normalize();
        }

        if (maxVelocity == 0) {
            return currentDirection;
        }
        float velocityFactor = Math.min(maxVelocity, characterControl.getWalkDirection().length()) / maxVelocity * acceleration;
        return currentDirection.add(targetDirection.mult(velocityFactor)).setY(0).normalize();

    }

    private float calculateNewVelocity(float currentVelocity) {
        if (targetVelocity > currentVelocity) {
            return Math.min(targetVelocity, currentVelocity + acceleration);
        } else if (targetVelocity < currentVelocity) {
            return Math.max(0, currentVelocity - acceleration);
        } else {
            return currentVelocity;
        }
    }

    private BetterCharacterControl getCharacterControl() {
        return unit.getNode().getControl(BetterCharacterControl.class);
    }

    public void lookTowards(WithNode target) {
        lookTowards(target.getLocation());
    }

    public void lookTowards(Vector3f location) {
        targetDirection = location
                .subtract(unit.getNode().getWorldTranslation()).setY(0)
                .normalize();
    }

    public void setTargetVelocity(float targetVelocity) {
        this.targetVelocity = Math.min(targetVelocity, maxVelocity);
    }

    public void setMaxTargetVelocity() {
        this.targetVelocity = maxVelocity;
    }

    public void setMaxVelocity(float maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public void setRotationOnlyOnMove(boolean rotationOnlyOnMove) {
        this.rotationOnlyOnMove = rotationOnlyOnMove;
    }

    @Override
    protected Object clone() {
        return new MovingControl(unit);
    }

}
