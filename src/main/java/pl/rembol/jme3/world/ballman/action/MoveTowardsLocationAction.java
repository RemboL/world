package pl.rembol.jme3.world.ballman.action;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.pathfinding.PathfindingService;
import pl.rembol.jme3.world.pathfinding.Rectangle2f;
import pl.rembol.jme3.world.pathfinding.paths.IExternalPath;
import pl.rembol.jme3.world.pathfinding.paths.VectorPath;

import com.jme3.animation.LoopMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class MoveTowardsLocationAction extends Action {

	private Vector2f rectangleStart;
	private Vector2f rectangleEnd;

	private Future<IExternalPath> newPathWorker;

	private IExternalPath newPath;

	private float targetDistance;

	@Autowired
	private Node rootNode;

	@Autowired
	PathfindingService pathfindingService;

	public MoveTowardsLocationAction init(Vector2f rectangleStart,
			Vector2f rectangleEnd, float targetDistance) {
		this.rectangleStart = rectangleStart;
		this.rectangleEnd = rectangleEnd;
		this.targetDistance = targetDistance;

		return this;
	}

	public MoveTowardsLocationAction init(Vector2f point, float targetDistance) {
		this.rectangleStart = point.subtract(targetDistance, targetDistance);
		this.rectangleEnd = point.add(new Vector2f(targetDistance,
				targetDistance));
		this.targetDistance = targetDistance;

		return this;
	}

	@Override
	protected void doAct(BallMan ballMan, float tpf) {

		if (newPath != null) {

			newPath.updatePath(ballMan.getLocation());

			Vector3f checkpoint = newPath.getCheckPoint();
			if (checkpoint != null) {
				ballMan.lookTowards(checkpoint);
				ballMan.setTargetVelocity(5f);
			}
		} else if (newPathWorker != null && newPathWorker.isDone()) {
			try {
				newPath = newPathWorker.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				newPathWorker = null;
			}
		}
	}

	@Override
	public void stop() {
		if (newPathWorker != null) {
			newPathWorker.cancel(true);
		}
		if (newPath != null) {
			newPath.clearPath();
		}
	}

	@Override
	public boolean isFinished(BallMan ballMan) {
		if (/*getClosest(ballMan.getLocation()).distance(ballMan.getLocation()) < targetDistance
				|| */(newPath != null && newPath
						.isFinished(ballMan.getLocation()))) {
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

	@Override
	protected void start(BallMan ballMan) {
		ballMan.setAnimation("walk", LoopMode.Loop);

		newPathWorker = pathfindingService.buildPath(ballMan.getLocation(),
				new Rectangle2f(rectangleStart, rectangleEnd));
	}

}
