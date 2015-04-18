package pl.rembol.jme3.world.ballman.action;

import org.springframework.beans.factory.annotation.Autowired;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.pathfinding.PathfindingService;
import pl.rembol.jme3.world.pathfinding.Rectangle2f;
import pl.rembol.jme3.world.pathfinding.paths.IExternalPath;

import com.jme3.animation.LoopMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class MoveTowardsTargetAction extends Action {

	private Vector3f targetPosition;
	private WithNode target;
	private float targetDistance;

	@Autowired
	private PathfindingService pathfindingService;

	private IExternalPath path;

	public MoveTowardsTargetAction init(WithNode target, float targetDistance) {
		this.target = target;
		this.targetDistance = targetDistance;

		// account for building width
		if (target instanceof Building) {
			this.targetDistance += Building.class.cast(target).getWidth();
		}
		this.targetPosition = target.getNode().getWorldTranslation().clone();

		return this;
	}

	@Override
	public void stop() {
		if (path != null) {
			path.clearPath();
		}
	}

	@Override
	protected void doAct(BallMan ballMan, float tpf) {
		path.updatePath(ballMan.getLocation());

		Vector3f checkpoint = path.getCheckPoint();
		if (checkpoint != null) {
			ballMan.lookTowards(checkpoint);
			ballMan.setTargetVelocity(5f);
		}

		if (targetPosition.distance(target.getNode().getWorldTranslation()) > targetDistance) {
			// target moved
			this.targetPosition = target.getNode().getWorldTranslation().clone();
			start(ballMan);
		}
	}

	@Override
	public boolean isFinished(BallMan ballMan) {
		if (ballMan.getLocation().distance(
				target.getNode().getWorldTranslation()) < targetDistance
				|| (path != null && path.isFinished(ballMan.getLocation()))) {
			ballMan.setTargetVelocity(0f);
			return true;
		}

		return false;
	}

	@Override
	protected void start(BallMan ballMan) {
		ballMan.setAnimation("walk", LoopMode.Loop);

		path = pathfindingService.buildPath(ballMan.getLocation(),
				new Rectangle2f(new Vector2f(targetPosition.x - targetDistance,
						targetPosition.z - targetDistance), new Vector2f(
						targetPosition.x + targetDistance, targetPosition.z
								+ targetDistance)));
	}
}
