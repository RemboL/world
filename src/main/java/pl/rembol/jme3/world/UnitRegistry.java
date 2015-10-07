package pl.rembol.jme3.world;

import com.jme3.collision.Collidable;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.building.house.House;
import pl.rembol.jme3.world.building.house.HouseControl;
import pl.rembol.jme3.world.building.toolshop.Toolshop;
import pl.rembol.jme3.world.building.warehouse.Warehouse;
import pl.rembol.jme3.world.input.state.SelectionManager;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.pathfinding.PathfindingService;
import pl.rembol.jme3.world.player.Player;
import pl.rembol.jme3.world.player.PlayerService;
import pl.rembol.jme3.world.save.UnitDTO;
import pl.rembol.jme3.world.save.UnitsDTO;
import pl.rembol.jme3.world.selection.Selectable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UnitRegistry implements ApplicationContextAware {

    private static final String UNIT_DATA_KEY = "unit_data_key";

    @Autowired
    private SelectionManager selectionManager;

    @Autowired
    private PathfindingService pathfindingService;

    @Autowired
    private PlayerService playerService;

    private ApplicationContext applicationContext;

    private int idSequence = 0;

    private boolean suspendRegistry = false;

    private Map<String, WithNode> units = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public List<? extends Collidable> getSelectablesNodes() {
        List<Collidable> collidables = new ArrayList<>();

        for (WithNode selectable : units.values()) {
            collidables.add(selectable.getNode());
        }
        return collidables;
    }

    public void register(WithNode selectable) {
        if (!suspendRegistry) {
            register(selectable, String.valueOf(idSequence++));
        }
    }

    public void register(WithNode selectable, String key) {
        units.put(key, selectable);
        selectable.getNode().setUserData(UNIT_DATA_KEY, key);

        if (selectable instanceof Solid) {
            pathfindingService.addSolid(selectable.getNode()
                    .getWorldTranslation(), selectable.getWidth());
        }
    }

    public void unregister(Selectable selectable) {
        units.remove(selectable.getNode().getUserData(UNIT_DATA_KEY));
        selectable.getNode().setUserData(UNIT_DATA_KEY, null);
        selectionManager.deselect(selectable);

        if (selectable instanceof Solid) {
            pathfindingService.removeSolid(selectable.getNode()
                    .getWorldTranslation(), selectable.getWidth());
        }
    }

    public WithNode getSelectable(Node node) {
        if (node != null && node.getUserData(UNIT_DATA_KEY) != null) {
            return units.get(node.getUserData(UNIT_DATA_KEY).toString());
        }
        return null;
    }

    public List<Building> getHousesByOwner(Player player) {
        return units.values().stream().filter(House.class::isInstance)
                .map(House.class::cast)
                .filter(house -> house.getOwner().equals(player))
                .filter(House::isConstructed).collect(Collectors.toList());
    }

    public int countHousing(Player player) {
        long count = units.values().stream().filter(BallMan.class::isInstance)
                .map(BallMan.class::cast)
                .filter(ballMan -> ballMan.getOwner().equals(player)).count();

        count += units.values().stream().filter(House.class::isInstance)
                .map(House.class::cast).map(House::control)
                .filter(control -> control != null)
                .filter(HouseControl::isRecruiting).count();

        return (int) count;
    }

    public List<Warehouse> getWarehousesByOwner(Player player) {
        return units.values().stream().filter(Warehouse.class::isInstance)
                .map(Warehouse.class::cast)
                .filter(warehouse -> warehouse.getOwner().equals(player))
                .filter(Warehouse::isConstructed).collect(Collectors.toList());
    }

    public List<Toolshop> getToolshopsByOwner(Player player) {
        return units.values().stream().filter(Toolshop.class::isInstance)
                .map(Toolshop.class::cast)
                .filter(toolshop -> toolshop.getOwner().equals(player))
                .filter(Toolshop::isConstructed).collect(Collectors.toList());
    }

    public List<Selectable> getSelectableByPosition(Vector3f start,
            Vector3f stop) {
        float minX = Math.min(start.x, stop.x);
        float maxX = Math.max(start.x, stop.x);
        float minZ = Math.min(start.z, stop.z);
        float maxZ = Math.max(start.z, stop.z);

        return units
                .values()
                .stream()
                .filter(Selectable.class::isInstance)
                .map(Selectable.class::cast)
                .filter(selectable -> selectable.getNode()
                        .getWorldTranslation().x >= minX)
                .filter(selectable -> selectable.getNode()
                        .getWorldTranslation().x <= maxX)
                .filter(selectable -> selectable.getNode()
                        .getWorldTranslation().z > minZ)
                .filter(selectable -> selectable.getNode()
                        .getWorldTranslation().z <= maxZ)
                .collect(Collectors.toList());
    }

    public List<WithNode> getSelectableByPosition(Vector3f start,
            Vector3f stop, float buffer) {
        float minX = Math.min(start.x, stop.x) - buffer;
        float maxX = Math.max(start.x, stop.x) + buffer;
        float minZ = Math.min(start.z, stop.z) - buffer;
        float maxZ = Math.max(start.z, stop.z) + buffer;

        return units
                .values()
                .stream()
                .filter(selectable -> selectable.getNode()
                        .getWorldTranslation().x >= minX)
                .filter(selectable -> selectable.getNode()
                        .getWorldTranslation().x <= maxX)
                .filter(selectable -> selectable.getNode()
                        .getWorldTranslation().z > minZ)
                .filter(selectable -> selectable.getNode()
                        .getWorldTranslation().z <= maxZ)
                .collect(Collectors.toList());
    }

    public boolean isSpaceFreeWithBuffer(Vector3f position, float width) {
        return isSpaceFree(position, width + 2.5f);
    }

    public boolean isSpaceFree(Vector3f position, float width) {
        return !units
                .values()
                .stream()
                .anyMatch(
                        selectable -> isColliding(selectable, position, width));
    }

    private boolean isColliding(WithNode selectable, Vector3f position,
            float width) {
        if (selectable.getNode().getWorldTranslation().x
                + selectable.getWidth() <= position.x - width) {
            return false;
        }
        if (selectable.getNode().getWorldTranslation().x
                - selectable.getWidth() >= position.x + width) {
            return false;
        }
        if (selectable.getNode().getWorldTranslation().z
                + selectable.getWidth() <= position.z - width) {
            return false;
        }
        if (selectable.getNode().getWorldTranslation().z
                - selectable.getWidth() >= position.z + width) {
            return false;
        }

        return true;
    }

    public UnitsDTO save() {

        return new UnitsDTO(idSequence, units.entrySet().stream()
                .map(entry -> entry.getValue().save(entry.getKey()))
                .collect(Collectors.toList()));
    }

    public void load(UnitsDTO units) {
        suspendRegistry = true;

        for (UnitDTO unit : units.getUnits()) {
            WithNode bean = applicationContext.getAutowireCapableBeanFactory()
                    .createBean(unit.getUnitClass());
            bean.load(unit);
            register(bean, unit.getKey());
        }
        idSequence = units.getIdSequence();

        suspendRegistry = false;

        playerService.players().forEach(Player::updateHousingLimit);
        playerService.players().forEach(Player::updateHousing);
    }
}
