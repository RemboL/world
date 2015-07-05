package pl.rembol.jme3.world.controls;

import pl.rembol.jme3.world.interfaces.WithNode;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class MovingControl extends AbstractControl {

    private Vector3f targetDirection;
    private float targetVelocity = 0;
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

        if (targetDirection != null) {
            characterControl.setViewDirection(characterControl
                    .getViewDirection().add(targetDirection).setY(0)
                    .normalize());
        }

        characterControl.setWalkDirection(characterControl.getViewDirection()
                .mult((targetVelocity + characterControl.getWalkDirection()
                        .length()) / 2));
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
        this.targetVelocity = targetVelocity;
    }

}
