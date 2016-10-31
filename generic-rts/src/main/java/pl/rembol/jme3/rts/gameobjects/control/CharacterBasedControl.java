package pl.rembol.jme3.rts.gameobjects.control;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainQuad;
import pl.rembol.jme3.rts.RtsGameState;

public class CharacterBasedControl extends BetterCharacterControl implements MovingPhysicsControl {
    
    private float acceleration = .1F;

    private float maxVelocity = 5f;
    private RtsGameState gameState;
    private PhysicsCollisionListener listener;

    public CharacterBasedControl(RtsGameState gameState, float radius, float height, float mass) {
        super(radius, height, mass);
        this.gameState = gameState;

        listener = event -> {
            if (event.getObjectA() == rigidBody) {
                if (!(event.getObjectB().getUserObject() instanceof TerrainQuad)) {
                    // DETECTED COLLISION
                }
            }
        };

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

        if (velocity != 0) {
            rigidBody.setFriction(1 - Math.max(Math.min(1F, velocity), 0F));

        }
    }

    @Override
    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    @Override
    public void setMaxVelocity(float maxVelocity) {
        this.maxVelocity = maxVelocity;
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

    @Override
    public void setPhysicsSpace(PhysicsSpace space) {
        super.setPhysicsSpace(space);

        if (space == null) {
            this.getPhysicsSpace().removeCollisionListener(listener);
        } else {
            this.getPhysicsSpace().addCollisionListener(listener);
        }

    }

}
