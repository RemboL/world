package pl.rembol.jme3.world.building;

import pl.rembol.jme3.world.GameRunningAppState;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class ConstructionSite extends AbstractControl {

	private Building building;
	private float buildingTimeInSeconds;
	private float progress = 0f;
	private boolean finished = false;

	private Vector3f finishedPosition;
	private Vector3f startingPosition;

	public ConstructionSite(Building building, GameRunningAppState appState,
			float buildingTimeInSeconds) {
		this.building = building;
		this.buildingTimeInSeconds = buildingTimeInSeconds;
		finishedPosition = appState.getTerrain().getGroundPosition(
				building.getNode().getLocalTranslation());
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
			building.getNode().setLocalTranslation(finishedPosition);
			building.getNode().removeControl(this);
		} else {

			building.getNode().setLocalTranslation(
					startingPosition.clone().interpolate(finishedPosition,
							progress / buildingTimeInSeconds));
		}
	}

}
