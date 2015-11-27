package pl.rembol.jme3.rts.gameobjects.action;

import com.jme3.animation.LoopMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.Solid;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithMovingControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;
import pl.rembol.jme3.rts.pathfinding.Rectangle2f;
import pl.rembol.jme3.rts.pathfinding.paths.IExternalPath;

public class MoveTowardsTargetAction extends Action<WithMovingControl> {

    private Vector3f targetPosition;
    private WithNode target;
    private float targetDistance;
    private WithMovingControl unit;

    private IExternalPath path;

    public MoveTowardsTargetAction(GameState gameState, WithMovingControl unit,
                                   WithNode target, float targetDistance) {
        super(gameState);
        this.unit = unit;
        this.target = target;
        this.targetDistance = targetDistance;

        // account for building width
        if (target instanceof Solid) {
            this.targetDistance += target.getWidth();
        }
        this.targetPosition = target.getNode().getWorldTranslation().clone();
    }

    @Override
    public void stop() {
        if (path != null) {
            path.clearPath();
        }
        unit.movingControl().setTargetVelocity(0f);
    }

    @Override
    protected void doAct(WithMovingControl unit, float tpf) {
        path.updatePath(new Vector2f(unit.getLocation().x, unit.getLocation().z));

        Vector2f checkpoint = path.getCheckPoint();
        if (checkpoint != null) {
            unit.movingControl().lookTowards(new Vector3f(checkpoint.x, 0, checkpoint.y));
            unit.movingControl().setMaxTargetVelocity();
        } else {
            unit.movingControl().setTargetVelocity(0f);
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
                || (path != null && path.isFinished(new Vector2f(unit.getLocation().x, unit.getLocation().z)))) {
            unit.movingControl().setTargetVelocity(0f);
            unit.movingControl().lookTowards(target);
            return true;
        }

        return false;
    }

    @Override
    protected boolean start(WithMovingControl unit) {
        unit.setAnimation("walk", LoopMode.Loop);

        if (target instanceof Solid) {
            path = gameState.pathfindingService.buildPath(
                    unit.getLocation(),
                    new Rectangle2f(new Vector2f(targetPosition.x
                            - target.getWidth() - 1, targetPosition.z
                            - target.getWidth() - 1), new Vector2f(
                            targetPosition.x + target.getWidth() + 1,
                            targetPosition.z + target.getWidth() + 1)));
        } else {
            path = gameState.pathfindingService.buildPath(unit.getLocation(),
                    new Rectangle2f(new Vector2f(targetPosition.x - 1,
                            targetPosition.z - 1), new Vector2f(
                            targetPosition.x + 1, targetPosition.z + 1)));
        }

        return true;
    }

}
