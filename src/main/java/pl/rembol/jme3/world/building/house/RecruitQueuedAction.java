package pl.rembol.jme3.world.building.house;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.ui.Picture;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;

public class RecruitQueuedAction {

    private final Picture actionIcon;

    private static final float TIME = 10;

    private final GameState gameState;

    private float progress = 0;

    public void addProgress(float progress) {
        this.progress += progress;
    }

    public boolean isFinished() {
        return progress >= TIME;
    }

    public RecruitQueuedAction(GameState gameState) {
        this.gameState = gameState;
        actionIcon = new Picture("recruit action icon");
        actionIcon.setImage(gameState.assetManager, "interface/icons/ballman.png", true);
        actionIcon.setWidth(32);
        actionIcon.setHeight(32);
    }

    public void execute(House house) {
        Vector2f location = new Vector2f(house.getLocation().x,
                house.getLocation().z);

        BallMan ballMan = new BallMan(gameState);
        ballMan.init(location.add(new Vector2f(
                FastMath.nextRandomFloat() * 2 - 11,
                FastMath.nextRandomFloat() * 2 - 1)));
        ballMan.setOwner(house.getOwner());
    }

    public float progress() {
        return progress / TIME;
    }

    public Picture getActionIcon() {
        return actionIcon;
    }
}
