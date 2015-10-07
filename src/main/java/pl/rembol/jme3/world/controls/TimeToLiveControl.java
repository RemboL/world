package pl.rembol.jme3.world.controls;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.springframework.context.ApplicationContext;

/**
 * Control that counts down time to live of given control. When time to live
 * reaches zero, the object is detached from parent.
 * 
 * @author RemboL
 */
public class TimeToLiveControl extends AbstractControl {

	private float timeToLive;
	private ApplicationContext applicationContext;

	public TimeToLiveControl(ApplicationContext applicationContext,
			float timeToLiveInSeconds) {
		this.applicationContext = applicationContext;
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
				applicationContext.getBean(BulletAppState.class)
						.getPhysicsSpace().remove(spatial);
			}
		}
	}

}
