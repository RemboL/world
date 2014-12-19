package pl.rembol.jme3.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * Control that counts down time to live of given control. When time to live
 * reaches zero, the object is detached from parent.
 * 
 * @author RemboL
 */
public class TimeToLiveControl extends AbstractControl {

	private float timeToLive;

	public TimeToLiveControl(float timeToLiveInSeconds) {
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
		}
	}

}
