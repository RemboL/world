package pl.rembol.jme3.world.ballmanunitregistry;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jme3.math.Vector3f;
import pl.rembol.jme3.rts.player.Player;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.building.house.House;
import pl.rembol.jme3.world.building.house.HouseControl;
import pl.rembol.jme3.world.building.toolshop.Toolshop;
import pl.rembol.jme3.world.building.warehouse.Warehouse;

public class BallManUnitRegistry {

    private final GameState gameState;

    public BallManUnitRegistry(GameState gameState) {
        this.gameState = gameState;
    }

    public List<Warehouse> getWarehousesByOwner(Player player) {
        return gameState.unitRegistry.unitsStream().filter(Warehouse.class::isInstance)
                .map(Warehouse.class::cast)
                .filter(warehouse -> warehouse.getOwner().equals(player))
                .filter(Warehouse::isConstructed).collect(Collectors.toList());
    }

    public List<Toolshop> getToolshopsByOwner(Player player) {
        return gameState.unitRegistry.unitsStream().filter(Toolshop.class::isInstance)
                .map(Toolshop.class::cast)
                .filter(toolshop -> toolshop.getOwner().equals(player))
                .filter(Toolshop::isConstructed).collect(Collectors.toList());
    }

    public Optional<Warehouse> getClosestWarehouse(final Vector3f location, Player player) {
        return getWarehousesByOwner(player)
                .stream()
                .sorted((first, second) -> Float.valueOf(
                        first.getNode().getWorldTranslation()
                                .distance(location)).compareTo(
                        second.getNode().getWorldTranslation()
                                .distance(location))).findFirst();
    }

    public Optional<Toolshop> getClosestToolshop(final Vector3f location, Player player) {

        return getToolshopsByOwner(player)
                .stream()
                .sorted((first, second) -> Float.valueOf(
                        first.getNode().getWorldTranslation()
                                .distance(location)).compareTo(
                        second.getNode().getWorldTranslation()
                                .distance(location))).findFirst();
    }

    public List<Building> getHousesByOwner(Player player) {
        return gameState.unitRegistry.unitsStream().filter(House.class::isInstance)
                .map(House.class::cast)
                .filter(house -> house.getOwner().equals(player))
                .filter(House::isConstructed).collect(Collectors.toList());
    }

    public int countHousing(Player player) {
        long count = gameState.unitRegistry.unitsStream().filter(BallMan.class::isInstance)
                .map(BallMan.class::cast)
                .filter(ballMan -> ballMan.getOwner().equals(player)).count();

        count += gameState.unitRegistry.unitsStream().filter(House.class::isInstance)
                .map(House.class::cast).map(House::control)
                .filter(control -> control != null)
                .filter(HouseControl::isRecruiting).count();

        return (int) count;
    }


}
