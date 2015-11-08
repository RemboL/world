package pl.rembol.jme3.world;

import com.jme3.math.Vector3f;
import pl.rembol.jme3.world.building.toolshop.Toolshop;
import pl.rembol.jme3.world.building.warehouse.Warehouse;
import pl.rembol.jme3.world.player.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BallManUnitRegistry extends UnitRegistry {
    public BallManUnitRegistry(GameState gameState) {
        super(gameState);
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
}
