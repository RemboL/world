package pl.rembol.jme3.rts.gameobjects.control;

import java.util.Optional;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;

public class MovingControl extends AbstractControl {

    private Vector3f targetDirection;

    private float targetVelocity = 0;

    private float maxVelocity = 5f;

    private WithNode unit;

    public MovingControl(WithNode unit) {
        this.unit = unit;
    }

    @Override
    protected void controlRender(RenderManager arg0, ViewPort arg1) {
    }

    @Override
    protected void controlUpdate(float tpf) {
        getCharacterControl().ifPresent(characterBasedControl -> {
            if (targetDirection != null) {
                characterBasedControl.turnTowards(targetDirection);
            }
            characterBasedControl.move(targetVelocity, targetDirection);

        });
    }

    private Optional<MovingPhysicsControl> getCharacterControl() {
        return Optional.ofNullable(unit.getNode().getControl(MovingPhysicsControl.class));
    }

    public void lookTowards(WithNode target) {
        lookTowards(target.getLocation());
    }

    public void lookTowards(Vector3f location) {
        targetDirection = location
                .subtract(unit.getNode().getWorldTranslation()).setY(0)
                .normalize();
    }

    public void stopMoving() {
        this.targetVelocity = 0F;
    }

    public void moveTowards(Vector3f location) {
        lookTowards(location);
        setMaxTargetVelocity();
    }

    public void setMaxTargetVelocity() {
        this.targetVelocity = maxVelocity;
    }

    public void setMaxVelocity(float maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public void setAcceleration(float acceleration) {
        getCharacterControl().ifPresent(characterBasedControl -> characterBasedControl.setAcceleration(acceleration));
    }

    @Override
    protected Object clone() {
        return new MovingControl(unit);
    }

}
