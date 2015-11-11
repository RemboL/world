package pl.rembol.jme3.world.building.house;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.ui.Picture;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.rts.player.Player;
import pl.rembol.jme3.world.resources.ResourceTypes;

public class RecruitQueuedAction {

    private final RecruitQueueIcon actionIcon;

    private static final float TIME = 10;

    private final GameState gameState;

    private HouseControl houseControl;

    private float progress = 0;

    public void addProgress(float progress) {
        this.progress += progress;
    }

    public boolean isFinished() {
        return progress >= TIME;
    }

    public RecruitQueuedAction(GameState gameState, HouseControl houseControl) {
        this.gameState = gameState;
        this.houseControl = houseControl;
        actionIcon = new RecruitQueueIcon(gameState, this);
    }

    public void execute(House house) {
        Vector2f location = new Vector2f(house.getLocation().x,
                house.getLocation().z);

        new BallMan(gameState, location.add(new Vector2f(
                FastMath.nextRandomFloat() * 2 - 11,
                FastMath.nextRandomFloat() * 2 - 1)), house.getOwner().getName());
    }

    public float progress() {
        return progress / TIME;
    }

    public Picture getActionIcon() {
        return actionIcon;
    }

    public void cancel() {
        houseControl.removeFromQueue(this);
        Player owner = houseControl.getHouse().getOwner();
        owner.setResource(ResourceTypes.HOUSING, gameState.ballManUnitRegistry.countHousing(owner));
    }

    HouseControl getControl() {
        return houseControl;
    }
}
