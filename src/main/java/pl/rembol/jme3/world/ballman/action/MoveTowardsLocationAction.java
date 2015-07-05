package pl.rembol.jme3.world.ballman.action;

import org.springframework.beans.factory.annotation.Autowired;

import pl.rembol.jme3.world.interfaces.WithMovingControl;
import pl.rembol.jme3.world.pathfinding.PathfindingService;
import pl.rembol.jme3.world.pathfinding.Rectangle2f;
import pl.rembol.jme3.world.pathfinding.paths.IExternalPath;

import com.jme3.animation.LoopMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class MoveTowardsLocationAction extends Action<WithMovingControl> {

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
    protected void doAct(WithMovingControl unit, float tpf) {
        path.updatePath(unit.getLocation());

        Vector3f checkpoint = path.getCheckPoint();
        if (checkpoint != null) {
            unit.movingControl().lookTowards(checkpoint);
            unit.movingControl().setTargetVelocity(5f);
        } else {
            unit.movingControl().setTargetVelocity(0f);
        }
    }

    @Override
    public void stop() {
        path.clearPath();
    }

    @Override
    public boolean isFinished(WithMovingControl unit) {
        if ((path != null && path.isFinished(unit.getLocation()))) {
            unit.movingControl().setTargetVelocity(0f);
            return true;
        }

        return false;
    }

    @Override
    protected boolean start(WithMovingControl unit) {
        unit.setAnimation("walk", LoopMode.Loop);

        path = pathfindingService.buildPath(unit.getLocation(),
                new Rectangle2f(rectangleStart, rectangleEnd));

        return true;
    }

}
