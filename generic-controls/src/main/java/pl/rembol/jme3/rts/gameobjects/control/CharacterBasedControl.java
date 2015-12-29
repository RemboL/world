package pl.rembol.jme3.rts.gameobjects.control;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;

public class CharacterBasedControl extends BetterCharacterControl implements MovingPhysicsControl {
    
    private float acceleration = .1F;

    public CharacterBasedControl(float radius, float height, float mass) {
        super(radius, height, mass);
    }

    @Override
    public void turnTowards(Vector3f direction) {
        Vector3f currentDirection = getViewDirection();
        setViewDirection(currentDirection.add(direction).setY(0).normalize());
    }

    @Override
    public void move(float velocity, Vector3f direction) {
        setWalkDirection(getViewDirection()
                .mult(calculateNewVelocity(velocity, getWalkDirection().length())));
    }

    @Override
    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    private float calculateNewVelocity(float targetVelocity, float currentVelocity) {
        if (targetVelocity > currentVelocity) {
            return Math.min(targetVelocity, currentVelocity + acceleration);
        } else if (targetVelocity < currentVelocity) {
            return Math.max(0, currentVelocity - acceleration);
        } else {
            return currentVelocity;
        }
    }


}
