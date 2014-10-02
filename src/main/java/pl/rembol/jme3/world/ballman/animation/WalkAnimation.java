package pl.rembol.jme3.world.ballman.animation;

import com.jme3.math.FastMath;

import pl.rembol.jme3.world.ballman.BallMan;

public class WalkAnimation implements Animation {

	private int frame = FastMath.nextRandomInt(0, 100);

	@Override
	public void tick(BallMan ballMan) {
		frame++;
		float maxAngle = (1f - (1f / (1f + ballMan.getWalkSpeed())))
				* FastMath.QUARTER_PI;

		float phase = frame * FastMath.TWO_PI / 100;

		ballMan.setLeftArmFlail(FastMath.sin(phase) * maxAngle, true);
		ballMan.setRightArmFlail(-FastMath.sin(phase) * maxAngle, true);
	}

}
