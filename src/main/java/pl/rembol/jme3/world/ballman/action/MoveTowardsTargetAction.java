package pl.rembol.jme3.world.ballman.action;

import org.springframework.beans.factory.annotation.Autowired;

import pl.rembol.jme3.world.Solid;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.interfaces.WithMovingControl;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.pathfinding.PathfindingService;
import pl.rembol.jme3.world.pathfinding.Rectangle2f;
import pl.rembol.jme3.world.pathfinding.paths.IExternalPath;

import com.jme3.animation.LoopMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class MoveTowardsTargetAction extends Action<WithMovingControl> {

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
    protected void doAct(WithMovingControl unit, float tpf) {
        path.updatePath(unit.getLocation());

        Vector3f checkpoint = path.getCheckPoint();
        if (checkpoint != null) {
            unit.control().lookTowards(checkpoint);
            unit.control().setTargetVelocity(5f);
        } else {
            unit.control().setTargetVelocity(0f);
        }

        if (targetPosition.distance(target.getNode().getWorldTranslation()) > targetDistance) {
            // target moved
            this.targetPosition = target.getNode().getWorldTranslation()
                    .clone();
            start(unit);
        }
    }

    @Override
    public boolean isFinished(WithMovingControl unit) {
        if (unit.getLocation().distance(target.getNode().getWorldTranslation()) < targetDistance
                || (path != null && path.isFinished(unit.getLocation()))) {
            unit.control().setTargetVelocity(0f);
            unit.control().lookTowards(target);
            return true;
        }

        return false;
    }

    @Override
    protected boolean start(WithMovingControl unit) {
        unit.setAnimation("walk", LoopMode.Loop);

        if (target instanceof Solid) {
            path = pathfindingService.buildPath(
                    unit.getLocation(),
                    new Rectangle2f(new Vector2f(targetPosition.x
                            - target.getWidth() - 2, targetPosition.z
                            - target.getWidth() - 2), new Vector2f(
                            targetPosition.x + target.getWidth() + 2,
                            targetPosition.z + target.getWidth() + 2)));
        } else {
            path = pathfindingService.buildPath(unit.getLocation(),
                    new Rectangle2f(new Vector2f(targetPosition.x - 1,
                            targetPosition.z - 1), new Vector2f(
                            targetPosition.x + 1, targetPosition.z + 1)));
        }

        return true;
    }

}
