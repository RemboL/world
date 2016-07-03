package pl.rembol.jme3.world.resources.deposits;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.resources.units.ResourceUnit;
import pl.rembol.jme3.rts.save.UnitDTO;
import pl.rembol.jme3.world.resources.units.FruitBasket;
import pl.rembol.jme3.world.save.FruitBushDTO;
import pl.rembol.jme3.world.smallobject.tools.Tool;

public class FruitBush extends ResourceDeposit {

    public FruitBush(GameState gameState) {
        super(gameState);
    }

    public float getWidth() {
        return 3f;
    }

    @Override
    public String getIconName() {
        return "tree";
    }

    @Override
    public UnitDTO save(String key) {
        return new FruitBushDTO(key, this);
    }

    @Override
    public void load(UnitDTO unit) {
        if (FruitBushDTO.class.isInstance(unit)) {
            init(new Vector2f(unit.getPosition().x, unit.getPosition().z));
            setHp(FruitBushDTO.class.cast(unit).getHp());
        }
    }

    @Override
    protected float getPhysicsRadius() {
        return 3f;
    }

    @Override
    protected String getModelFileName() {
        return "fruitbush/fruitbush.mesh.xml";
    }

    @Override
    protected String getName() {
        return "Fruit Bush";
    }

    @Override
    protected RandomDirectionMode getRandomDirectionMode() {
        return RandomDirectionMode.WHOLE_CIRCLE;
    }

    @Override
    public ResourceUnit produceResource() {
        return new FruitBasket(gameState, getLocation(), 0);
    }

    @Override
    public Class<? extends Tool> requiredTool() {
        return null;
    }

    @Override
    public Class<? extends ResourceUnit> givesResource() {
        return FruitBasket.class;
    }

}
