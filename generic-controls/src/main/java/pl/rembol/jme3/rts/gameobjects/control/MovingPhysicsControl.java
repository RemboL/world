package pl.rembol.jme3.rts.gameobjects.control;

import com.jme3.math.Vector3f;
import com.jme3.scene.control.Control;

public interface MovingPhysicsControl extends Control {
    
    void turnTowards(Vector3f direction);
    
    void move(float velocity, Vector3f direction);
    
    void setAcceleration(float acceleration);

    void setMaxVelocity(float maxVelocity);
}
