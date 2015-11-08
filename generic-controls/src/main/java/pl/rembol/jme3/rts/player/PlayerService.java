package pl.rembol.jme3.rts.player;

import com.jme3.math.ColorRGBA;
import pl.rembol.jme3.rts.gui.ResourcesBar;
import pl.rembol.jme3.rts.gui.console.ConsoleLog;
import pl.rembol.jme3.rts.resources.ResourceType;
import pl.rembol.jme3.rts.save.PlayerDTO;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerService {

    private final ResourcesBar resourcesBar;
    private final ConsoleLog consoleLog;

    private Map<String, Player> players = new HashMap<>();

    private Player activePlayer;

    private List<ResourceType> resourceTypeList;

    public PlayerService(ResourcesBar resourcesBar, ConsoleLog consoleLog) {
        this.resourcesBar = resourcesBar;
        this.consoleLog = consoleLog;
    }

    public void registerPlayer(String name, ColorRGBA color, boolean active) {

        if (players.containsKey(name)) {
            System.out.println("player with name \"" + name
                    + "\" already registered.");
            return;
        }

        Player player = new Player(resourcesBar, consoleLog, resourceTypeList);
        player.setName(name);
        player.setColor(color);
        player.setActive(active);
        if (active) {
            activePlayer = player;
        }

        players.put(player.getName(), player);
    }

    public Player getPlayer(String name) {
        return players.get(name);
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public List<PlayerDTO> savePlayers() {
        return players.values().stream().map(PlayerDTO::new)
                .collect(Collectors.toList());
    }

    public void loadPlayers(List<PlayerDTO> playerDTOs) {
        for (PlayerDTO playerDTO : playerDTOs) {
            registerPlayer(playerDTO.getName(), playerDTO.getColor(),
                    playerDTO.getActive());
        }

    }

    public Collection<Player> players() {
        return players.values();
    }

    public void setResourceTypeList(List<ResourceType> resourceTypeList) {
        this.resourceTypeList = resourceTypeList;
    }
}
