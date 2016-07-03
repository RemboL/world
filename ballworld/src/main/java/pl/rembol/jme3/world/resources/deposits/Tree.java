package pl.rembol.jme3.world.resources.deposits;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.resources.units.ResourceUnit;
import pl.rembol.jme3.rts.save.UnitDTO;
import pl.rembol.jme3.world.resources.units.Log;
import pl.rembol.jme3.world.save.TreeDTO;
import pl.rembol.jme3.world.smallobject.tools.Axe;
import pl.rembol.jme3.world.smallobject.tools.Tool;

public class Tree extends ResourceDeposit {

    public Tree(GameState gameState) {
        super(gameState);
    }

    public float getWidth() {
        return 2f;
    }

    @Override
    public String getIconName() {
        return "tree";
    }

    @Override
    public UnitDTO save(String key) {
        return new TreeDTO(key, this);
    }

    @Override
    public void load(UnitDTO unit) {
        if (TreeDTO.class.isInstance(unit)) {
            init(new Vector2f(unit.getPosition().x, unit.getPosition().z));
            setHp(TreeDTO.class.cast(unit).getHp());
        }
    }

    @Override
    protected float getPhysicsRadius() {
        return 1.5f;
    }

    @Override
    protected String getModelFileName() {
        return "tree.blend";
    }

    @Override
    protected String getName() {
        return "Tree";
    }

    @Override
    protected RandomDirectionMode getRandomDirectionMode() {
        return RandomDirectionMode.WHOLE_CIRCLE;
    }

    @Override
    public ResourceUnit produceResource() {
        return new Log(gameState, getLocation(), 0);
    }

    @Override
    public Class<? extends Tool> requiredTool() {
        return Axe.class;
    }

    @Override
    public Class<? extends ResourceUnit> givesResource() {
        return Log.class;
    }

}
