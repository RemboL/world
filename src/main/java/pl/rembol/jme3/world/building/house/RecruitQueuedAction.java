package pl.rembol.jme3.world.building.house;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.world.ballman.BallMan;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

public class RecruitQueuedAction {

    private static final float TIME = 10;

    private float progress = 0;

    public void addProgress(float progress) {
        this.progress += progress;
    }

    public boolean isFinished() {
        return progress >= TIME;
    }

    public void execute(ApplicationContext applicationContext, House house) {
        Vector2f location = new Vector2f(house.getLocation().x,
                house.getLocation().z);

        BallMan ballMan = applicationContext.getAutowireCapableBeanFactory()
                .createBean(BallMan.class);
        ballMan.init(location.add(new Vector2f(
                FastMath.nextRandomFloat() * 2 - 11,
                FastMath.nextRandomFloat() * 2 - 1)));
        ballMan.setOwner(house.getOwner());
    }

    public int progressPercent() {
        return Math.round(progress / TIME * 100);
    }
}
