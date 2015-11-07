package pl.rembol.jme3.rts.unit.control;

import com.jme3.bullet.control.PhysicsControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.rts.GameState;

/**
 * Control that counts down time to live of given control. When time to live
 * reaches zero, the object is detached from parent.
 *
 * @author RemboL
 */
public class TimeToLiveControl extends AbstractControl {

    private GameState gameState;

    private float timeToLive;

    public TimeToLiveControl(GameState gameState,
                             float timeToLiveInSeconds) {
        this.gameState = gameState;
        this.timeToLive = timeToLiveInSeconds;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    protected void controlUpdate(float tpf) {
        timeToLive -= tpf;

        if (timeToLive < 0) {
            spatial.getParent().detachChild(spatial);
            if (spatial.getControl(PhysicsControl.class) != null) {
                gameState.bulletAppState
                        .getPhysicsSpace().remove(spatial);
            }
        }
    }

}
