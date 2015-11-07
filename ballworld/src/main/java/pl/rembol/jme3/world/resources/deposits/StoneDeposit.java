package pl.rembol.jme3.world.resources.deposits;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.rts.resources.units.ResourceUnit;
import pl.rembol.jme3.world.resources.units.StoneBrick;
import pl.rembol.jme3.world.save.StoneDepositDTO;
import pl.rembol.jme3.rts.save.UnitDTO;
import pl.rembol.jme3.world.smallobject.tools.PickAxe;
import pl.rembol.jme3.world.smallobject.tools.Tool;

import java.util.Optional;

public class StoneDeposit extends ResourceDeposit {

    public StoneDeposit(GameState gameState) {
        super(gameState);
    }

    @Override
    public String getIconName() {
        return "stone_deposit";
    }

    @Override
    public float getWidth() {
        return 5f;
    }

    @Override
    protected float getPhysicsRadius() {
        return 3f;
    }

    @Override
    protected String getModelFileName() {
        return "stone_deposit.blend";
    }

    @Override
    protected String getName() {
        return "Stone Deposit";
    }

    @Override
    protected RandomDirectionMode getRandomDirectionMode() {
        return RandomDirectionMode.ONLY_4_DIRECTIONS;
    }

    @Override
    public Class<? extends ResourceUnit> givesResource() {
        return StoneBrick.class;
    }

    @Override
    public ResourceUnit produceResource() {
        return new StoneBrick(gameState, getLocation(), 0);
    }

    @Override
    public UnitDTO save(String key) {
        return new StoneDepositDTO(key, this);
    }

    @Override
    public void load(UnitDTO unit) {
        if (StoneDepositDTO.class.isInstance(unit)) {
            init(new Vector2f(unit.getPosition().x, unit.getPosition().z));
            setHp(StoneDepositDTO.class.cast(unit).getHp());
        }
    }

    @Override
    public Optional<Class<? extends Tool>> requiredTool() {
        return Optional.of(PickAxe.class);
    }

}
