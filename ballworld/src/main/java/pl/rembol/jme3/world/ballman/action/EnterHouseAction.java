package pl.rembol.jme3.world.ballman.action;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.unit.action.Action;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.house.House;

public class EnterHouseAction extends Action<BallMan> {

    private House house;
    
    private Vector2f enteringPosition; 
    
    public EnterHouseAction(GameState gameState, House house) {
        super(gameState);
        this.house = house;
        
        this.enteringPosition = house.getEnteringLocation();
    }

    @Override
    protected boolean start(BallMan ballMan) {
        if (!assertDistance(ballMan, enteringPosition, 0f)) {
            return false;
        }

        return true;
    }

    @Override
    protected void doAct(BallMan unit, float tpf) {
        house.enter(unit);
    }

    public boolean isFinished(BallMan ballMan) {
        return isStarted;
    }


}
