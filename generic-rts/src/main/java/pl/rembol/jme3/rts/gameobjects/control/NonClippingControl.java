package pl.rembol.jme3.rts.gameobjects.control;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.rts.RtsGameState;

public class NonClippingControl extends RigidBodyControl implements MovingPhysicsControl {

    private float acceleration = .1F;

    private float maxVelocity = 5F;

    private float currentVelocity = 0F;

    private float targetVelocity = 0F;

    private final RtsGameState gameState;

    public NonClippingControl(RtsGameState gameState, float radius) {
        super(new SphereCollisionShape(radius), 0);
        this.gameState = gameState;
    }

    @Override
    public void turnTowards(Vector3f direction) {
        if (direction != null) {
            float rotation = FastMath.atan2(-direction.z, direction.x) + FastMath.HALF_PI;

            this.setPhysicsRotation(new Quaternion().fromAngleNormalAxis(rotation, Vector3f.UNIT_Y));
        }
    }

    @Override
    public void move(float velocity, Vector3f direction) {
        targetVelocity = velocity;

        turnTowards(direction);
    }

    @Override
    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    @Override
    public void setMaxVelocity(float maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    private void calculateNewVelocity() {
        if (targetVelocity > currentVelocity) {
            currentVelocity = Math.min(Math.min(targetVelocity, maxVelocity), currentVelocity + acceleration);
        } else if (targetVelocity < currentVelocity) {
            currentVelocity = Math.max(0, currentVelocity - acceleration);
        }
    }

    @Override
    public void update(float tpf) {
        calculateNewVelocity();

        Vector3f newPosition = getPhysicsLocation().add(getPhysicsRotation().mult(Vector3f.UNIT_Z).mult(currentVelocity * tpf));
        newPosition = gameState.terrain.getGroundPosition(newPosition);

        this.setPhysicsLocation(newPosition);

        super.update(tpf);
    }

}