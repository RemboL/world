package pl.rembol.jme3.rts.gameobjects.control;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CharacterBasedControl extends BetterCharacterControl implements MovingPhysicsControl {

    private float acceleration = .1F;

    private float maxVelocity = 5f;
    private RtsGameState gameState;
    private PhysicsCollisionListener listener;
    private boolean isStationary = true;

    private Map<WithNode, Long> collisionsCache = new HashMap<>();

    public CharacterBasedControl(RtsGameState gameState, float radius, float height, float mass) {
        super(radius, height, mass);
        this.gameState = gameState;

        listener = event -> {
            if (!itsMyCollision(event)) {
                return;
            }

            PhysicsCollisionObject other = getOther(event);

            if (!(other.getUserObject() instanceof TerrainQuad)) {
                if (other.getUserObject() instanceof Node) {
                    Node otherNode = ((Node) other.getUserObject());
                    WithNode otherObject = gameState.unitRegistry.getSelectable(otherNode);
                    collisionsCache.put(otherObject, System.currentTimeMillis());
                }
            }
        };
    }

    private boolean itsMyCollision(PhysicsCollisionEvent event) {
        return event.getObjectA() == rigidBody || event.getObjectB() == rigidBody;
    }

    private PhysicsCollisionObject getOther(PhysicsCollisionEvent event) {
        if (event.getObjectA() == rigidBody) {
            return event.getObjectB();
        }
        return event.getObjectA();
    }

    @Override
    public void turnTowards(Vector3f direction) {
        Vector3f currentDirection = getViewDirection();
        setViewDirection(currentDirection.add(direction).setY(0).normalize());
    }

    @Override
    public void move(float velocity, Vector3f direction) {

        float newVelocity = calculateNewVelocity(velocity, getWalkDirection().length());

        invalidateCollidablesCache();

        Vector3f collidablesRepulsion = calculateCollidablesRepulsion();
        if (collidablesRepulsion.length() > 1) {
            collidablesRepulsion.normalizeLocal();
        }
        if (collidablesRepulsion.length() > 0.1F) {
            float repulsionAngle = collidablesRepulsion.normalize().angleBetween(getViewDirection().normalize());
            if (repulsionAngle > FastMath.HALF_PI) { // if collision is bumping me almost backwards, try to veer to the right
                float backwardsRepulsionFactor = (repulsionAngle - FastMath.HALF_PI) / FastMath.HALF_PI;
                collidablesRepulsion.add(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y).mult(getViewDirection().mult(backwardsRepulsionFactor)));
            }
        }
        collidablesRepulsion.multLocal(newVelocity);

        setWalkDirection(getViewDirection().add(collidablesRepulsion).normalize().mult(newVelocity));

        if (velocity < 0.01f && !isStationary) {
            setStationary();
        } else {
            if (velocity > 0.01f && isStationary) {
                setNonStationary();
            }
        }
    }

    private Vector3f calculateCollidablesRepulsion() {
        return collisionsCache.keySet().stream().map(other -> {
            float distance = getSpatialTranslation().distance(other.getLocation());
            float factor = 0;
            if (distance < getFinalRadius() + other.getWidth() + 1) {
                distance -= getFinalRadius() + other.getWidth() - 1;
                if (distance < 0.5F) {
                    factor = 1;
                } else {
                    factor = 0.5F / distance;
                }
            }
            return getSpatialTranslation().subtract(other.getLocation()).setY(0).normalize().mult(factor * factor);
        }).reduce(Vector3f.ZERO, Vector3f::add);
    }

    private void invalidateCollidablesCache() {
        long now = System.currentTimeMillis();
        Set<WithNode> toBeRemoved = collisionsCache.entrySet().stream().filter(entry -> now - entry.getValue() > 1_000).map(Map.Entry::getKey).collect(Collectors.toSet());
        toBeRemoved.forEach(collisionsCache::remove);
    }

    private void setStationary() {
        rigidBody.setKinematic(true);
        isStationary = true;
    }

    private void setNonStationary() {
        rigidBody.setKinematic(false);
        isStationary = false;
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
