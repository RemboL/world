package pl.rembol.jme3.world.ballman.order;

import static pl.rembol.jme3.world.resources.ResourceType.HOUSING;
import static pl.rembol.jme3.world.resources.ResourceType.WOOD;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.resources.Cost;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

public class RecruitOrder extends Order<Building> {

    @Override
    protected void doPerform(Building house, Vector2f location) {
        doPerform(house);
    }

    @Override
    protected void doPerform(Building house, WithNode target) {
        doPerform(house);
    }

    private void doPerform(Building house) {
        if (house.getOwner().retrieveResources(
                new Cost().of(WOOD, 50).and(HOUSING, 1))) {

            Vector2f location = new Vector2f(house.getLocation().x,
                    house.getLocation().z);

            BallMan ballMan = applicationContext
                    .getAutowireCapableBeanFactory().createBean(BallMan.class);
            ballMan.init(location.add(new Vector2f(-10, 0)).add(
                    new Vector2f(FastMath.rand.nextFloat() * 2 - 1,
                            FastMath.rand.nextFloat() * 2 - 1)));
            ballMan.setOwner(house.getOwner());
        }
    }
}
