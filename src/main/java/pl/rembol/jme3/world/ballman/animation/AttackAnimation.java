package pl.rembol.jme3.world.ballman.animation;

import com.jme3.math.FastMath;

import pl.rembol.jme3.world.ballman.BallMan;

public class AttackAnimation implements Animation {
	
	private int frame = 0;
	
	@Override
	public void tick(BallMan ballMan) {
		frame++;
		ballMan.setLeftArmFlail(0, false);
		if (frame % 100 == 1) {
			ballMan.setRightArmFlail(-FastMath.QUARTER_PI * 4, false);
		} else if (frame % 100 == 51) {
			ballMan.setRightArmFlail(0, false);
		}
	}

}
