package pl.rembol.jme3.world.building.house;

import java.util.ArrayList;
import java.util.List;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.resources.ResourceTypes;

public class HouseControl extends AbstractControl {

    private static final int MAX_QUEUE_SIZE = 5;
    private List<RecruitQueuedAction> queue = new ArrayList<>();
    private GameState gameState;
    private House house;
    private List<BallMan> ballMenInside = new ArrayList<>();

    public HouseControl(GameState gameState, House house) {
        this.gameState = gameState;
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
            currentAction.execute(house);
            queue.remove(currentAction);

            house.getOwner().setResource(ResourceTypes.HOUSING, gameState.unitRegistry.countHousing(house.getOwner()));
        }

        gameState.selectionManager.updateStatusIfSingleSelected(house);
    }

    public boolean canAddToQueue() {
        if (queue.size() >= MAX_QUEUE_SIZE) {
            gameState.consoleLog.addLine("Queue is full");
            return false;
        }
        return true;
    }

    public void addToQueue() {
        queue.add(new RecruitQueuedAction(gameState, this));

        house.getOwner().setResource(ResourceTypes.HOUSING, gameState.unitRegistry.countHousing(house.getOwner()));
    }

    public boolean isRecruiting() {
        return !queue.isEmpty();
    }

    public List<RecruitQueuedAction> getQueue() {
        return queue;
    }

    public void removeFromQueue(RecruitQueuedAction recruitQueuedAction) {
        queue.remove(recruitQueuedAction);
        gameState.selectionManager.updateStatusIfSingleSelected(house);
    }
    
    House getHouse() {
        return house;
    }
    
    public void enter(BallMan ballMan) {
        ballMenInside.add(ballMan);
    }
    
    public void exit(BallMan ballMan) {
        ballMenInside.remove(ballMan);
    }
}
