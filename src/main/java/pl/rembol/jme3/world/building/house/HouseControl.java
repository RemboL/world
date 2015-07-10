package pl.rembol.jme3.world.building.house;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.world.hud.ConsoleLog;
import pl.rembol.jme3.world.input.state.SelectionManager;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class HouseControl extends AbstractControl {

    private static final int MAX_QUEUE_SIZE = 5;
    private List<RecruitQueuedAction> queue = new ArrayList<>();
    private ApplicationContext applicationContext;
    private House house;

    public HouseControl(ApplicationContext applicationContext, House house) {
        this.applicationContext = applicationContext;
        this.house = house;

    }

    @Override
    protected void controlRender(RenderManager arg0, ViewPort arg1) {
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (queue.isEmpty()) {
            return;
        }

        RecruitQueuedAction currentAction = queue.get(0);

        currentAction.addProgress(tpf);
        if (currentAction.isFinished()) {
            currentAction.execute(applicationContext, house);
            queue.remove(currentAction);

            house.getOwner().updateHousing();
        }

        applicationContext.getBean(SelectionManager.class)
                .updateStatusIfSingleSelected(house);
    }

    public boolean canAddToQueue() {
        if (queue.size() >= MAX_QUEUE_SIZE) {
            applicationContext.getBean(ConsoleLog.class).addLine(
                    "Queue is full");
            return false;
        }
        return true;
    }

    public void addToQueue() {
        queue.add(new RecruitQueuedAction());

        house.getOwner().updateHousing();
    }

    public boolean isRecruiting() {
        return !queue.isEmpty();
    }

    public String recruitingStatus() {
        if (queue.isEmpty()) {
            return null;
        }

        String recruitingStatus = "Recruiting... "
                + queue.get(0).progressPercent() + "%";

        if (queue.size() > 1) {
            recruitingStatus += " + " + (queue.size() - 1) + " more";
        }

        return recruitingStatus;
    }
}
