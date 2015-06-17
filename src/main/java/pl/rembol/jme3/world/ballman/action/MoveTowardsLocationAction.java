package pl.rembol.jme3.world.ballman.action;

import org.springframework.beans.factory.annotation.Autowired;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.pathfinding.PathfindingService;
import pl.rembol.jme3.world.pathfinding.Rectangle2f;
import pl.rembol.jme3.world.pathfinding.paths.IExternalPath;

import com.jme3.animation.LoopMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class MoveTowardsLocationAction extends Action {

    private Vector2f rectangleStart;
    private Vector2f rectangleEnd;

    private IExternalPath path;

    @Autowired
    private Node rootNode;

    @Autowired
    private PathfindingService pathfindingService;

    public MoveTowardsLocationAction init(Vector2f point, float targetDistance) {
        this.rectangleStart = point.subtract(targetDistance, targetDistance);
        this.rectangleEnd = point.add(new Vector2f(targetDistance,
                targetDistance));

        return this;
    }

    @Override
    protected void doAct(BallMan ballMan, float tpf) {
        path.updatePath(ballMan.getLocation());

        Vector3f checkpoint = path.getCheckPoint();
        if (checkpoint != null) {
            ballMan.lookTowards(checkpoint);
            ballMan.setTargetVelocity(5f);
        } else {
            ballMan.setTargetVelocity(0f);
        }
    }

    @Override
    public void stop() {
        path.clearPath();
    }

    @Override
    public boolean isFinished(BallMan ballMan) {
        if ((path != null && path.isFinished(ballMan.getLocation()))) {
            ballMan.setTargetVelocity(0f);
            return true;
        }

        return false;
    }

    @Override
    protected boolean start(BallMan ballMan) {
        ballMan.setAnimation("walk", LoopMode.Loop);

        path = pathfindingService.buildPath(ballMan.getLocation(),
                new Rectangle2f(rectangleStart, rectangleEnd));

        return true;
    }

}
