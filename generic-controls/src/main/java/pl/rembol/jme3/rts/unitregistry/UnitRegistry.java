package pl.rembol.jme3.rts.unitregistry;

import com.jme3.collision.Collidable;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.events.unitdestroyed.UnitDestroyedEvent;
import pl.rembol.jme3.rts.player.Player;
import pl.rembol.jme3.rts.save.UnitDTO;
import pl.rembol.jme3.rts.save.UnitsDTO;
import pl.rembol.jme3.rts.unit.interfaces.Solid;
import pl.rembol.jme3.rts.unit.interfaces.WithNode;
import pl.rembol.jme3.rts.unit.selection.Selectable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnitRegistry {

    public static final String UNIT_DATA_KEY = "unit_data_key";

    protected GameState gameState;

    private int idSequence = 0;

    private boolean suspendRegistry = false;

    protected Map<String, WithNode> units = new HashMap<>();

    public UnitRegistry(GameState gameState) {
        this.gameState = gameState;
    }

    public List<? extends Collidable> getSelectablesNodes() {
        return units.values().stream().map(WithNode::getNode).collect(Collectors.toList());
    }

    public Stream<WithNode> unitsStream() {
        return units.values().stream();
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
            gameState.pathfindingService.addSolid(selectable.getNode()
                    .getWorldTranslation(), selectable.getWidth());
        }
    }

    public void unregister(Selectable selectable) {
        gameState.eventManager.sendEvent(new UnitDestroyedEvent(selectable));

        units.remove(selectable.getNode().getUserData(UNIT_DATA_KEY));
        selectable.getNode().setUserData(UNIT_DATA_KEY, null);

        if (selectable instanceof Solid) {
            gameState.pathfindingService.removeSolid(selectable.getNode()
                    .getWorldTranslation(), selectable.getWidth());
        }
    }

    public WithNode getSelectable(Node node) {
        if (node != null && node.getUserData(UNIT_DATA_KEY) != null) {
            return units.get(node.getUserData(UNIT_DATA_KEY).toString());
        }
        return null;
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
            WithNode bean = unit.produce(gameState);
            bean.load(unit);
            register(bean, unit.getKey());
        }
        idSequence = units.getIdSequence();

        suspendRegistry = false;

        gameState.playerService.players().forEach(Player::updateResources);
    }

    public void registerUserData(WithNode target, String dataKey, WithNode object) {
        units.entrySet().stream()
                .filter(entry -> entry.getValue() == object)
                .map(Map.Entry::getKey)
                .findFirst()
                .ifPresent(objectKey -> target.getNode().setUserData(dataKey, objectKey));
    }

    public Optional<WithNode> getUserData(WithNode target, String dataKey) {
        return Optional.ofNullable(units.get(target.getNode().<String>getUserData(dataKey)));
    }

    public void clearUserData(WithNode withNode, String dataKey) {
        withNode.getNode().setUserData(dataKey, null);
    }
}
