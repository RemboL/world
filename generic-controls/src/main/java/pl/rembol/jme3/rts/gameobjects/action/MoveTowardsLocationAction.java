package pl.rembol.jme3.rts.gameobjects.action;

import com.jme3.animation.LoopMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithMovingControl;
import pl.rembol.jme3.geom.Rectangle2f;
import pl.rembol.jme3.rts.pathfinding.paths.IExternalPath;

public class MoveTowardsLocationAction extends Action<WithMovingControl> {

    private Vector2f rectangleStart;
    private Vector2f rectangleEnd;

    private IExternalPath path;
    private WithMovingControl unit;

    public MoveTowardsLocationAction(GameState gameState, WithMovingControl unit, Vector2f point, float targetDistance) {
        super(gameState);
        this.unit = unit;
        this.rectangleStart = point.subtract(targetDistance, targetDistance);
        this.rectangleEnd = point.add(new Vector2f(targetDistance,
                targetDistance));
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
    }

    @Override
    public void stop() {
        if (path != null) {
            path.clearPath();
        }
        unit.movingControl().setTargetVelocity(0f);
    }

    @Override
    public boolean isFinished(WithMovingControl unit) {
        if ((path != null && path.isFinished(new Vector2f(unit.getLocation().x, unit.getLocation().z)))) {
            unit.movingControl().setTargetVelocity(0f);
            return true;
        }

        return false;
    }

    @Override
    protected boolean start(WithMovingControl unit) {
        unit.setAnimation("walk", LoopMode.Loop);

        path = gameState.pathfindingService.buildPath(unit.getLocation(),
                new Rectangle2f(rectangleStart, rectangleEnd));

        return true;
    }

}
