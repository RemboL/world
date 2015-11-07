package pl.rembol.jme3.world.building;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.world.GameState;

public class ConstructionSite extends AbstractControl {

    private GameState gameState;
    private Building building;
    private float buildingTimeInSeconds;
    private float progress = 0f;
    private boolean finished = false;

    private Vector3f finishedPosition;
    private Vector3f startingPosition;

    public ConstructionSite(GameState gameState, Building building, float buildingTimeInSeconds) {
        this.gameState = gameState;
        this.building = building;
        this.buildingTimeInSeconds = buildingTimeInSeconds;
        finishedPosition = new Vector3f(0, 0, 0);
        startingPosition = finishedPosition.subtract(Vector3f.UNIT_Y
                .mult(building.getHeight()));
        building.getNode().addControl(this);
    }

    public void addBuildProgress(float progress) {
        this.progress += progress;

        if (this.progress >= buildingTimeInSeconds) {
            finished = true;
        }

    }

    public boolean isFinished() {
        return finished;
    }

    public Building getBuilding() {
        return building;
    }

    @Override
    protected void controlRender(RenderManager paramRenderManager,
                                 ViewPort paramViewPort) {

    }

    @Override
    protected void controlUpdate(float tpf) {
        if (finished) {
            finishBuilding();
        } else {
            building.getNode().setLocalTranslation(
                    startingPosition.clone().interpolate(finishedPosition,
                            progress / buildingTimeInSeconds));
        }
    }

    private void finishBuilding() {
        building.getNode().setLocalTranslation(finishedPosition);
        building.getNode().removeControl(this);

        building.finishBuilding();

        gameState.actionBox.updateActionButtons();
    }

}
