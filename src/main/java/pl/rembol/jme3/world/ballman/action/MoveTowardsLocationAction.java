package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.ballman.BallMan;

import com.jme3.animation.LoopMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class MoveTowardsLocationAction extends Action {

	private Vector2f rectangleStart;
	private Vector2f rectangleEnd;

	private float targetDistance;

	public MoveTowardsLocationAction(Vector2f rectangleStart,
			Vector2f rectangleEnd, float targetDistance) {
		this.rectangleStart = rectangleStart;
		this.rectangleEnd = rectangleEnd;
		this.targetDistance = targetDistance;
	}

	public MoveTowardsLocationAction(Vector2f point, float targetDistance) {
		this.rectangleStart = point;
		this.rectangleEnd = point;
		this.targetDistance = targetDistance;
	}

	@Override
	protected void doAct(BallMan ballMan) {
		ballMan.lookTowards(getClosest(ballMan.getLocation()));
		ballMan.setTargetVelocity(5f);
	}

	@Override
	public boolean isFinished(BallMan ballMan) {
		if (getClosest(ballMan.getLocation()).distance(ballMan.getLocation()) < targetDistance) {
			ballMan.setTargetVelocity(0f);
			return true;
		}

		return false;
	}

	private Vector3f getClosest(Vector3f location) {
		Vector2f closest2f = getClosest(new Vector2f(location.x, location.z));
		return new Vector3f(closest2f.x, location.y, closest2f.y);
	}

	private Vector2f getClosest(Vector2f vector2f) {
		return new Vector2f(getClosest(rectangleStart.x, rectangleEnd.x,
				vector2f.x), getClosest(rectangleStart.y, rectangleEnd.y,
				vector2f.y));
	}

	private float getClosest(float start, float end, float position) {
		if (position < start) {
			return start;
		}

		if (start <= position && position <= end) {
			return position;
		}

		return end;
	}

	protected void start(BallMan ballMan) {
		ballMan.setAnimation("walk", LoopMode.Loop);
	}

}
