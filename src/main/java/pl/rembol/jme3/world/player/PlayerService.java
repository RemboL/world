package pl.rembol.jme3.world.player;

import com.jme3.math.ColorRGBA;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.resources.ResourceType;
import pl.rembol.jme3.world.save.PlayerDTO;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerService {
    private final GameState gameState;

    private Map<String, Player> players = new HashMap<>();

    private Player activePlayer;

    public PlayerService(GameState gameState) {
        this.gameState = gameState;
    }

    public void registerPlayer(String name, ColorRGBA color, boolean active) {

        if (players.containsKey(name)) {
            System.out.println("player with name \"" + name
                    + "\" already registered.");
            return;
        }

        Player player = new Player(gameState);
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
            getPlayer(playerDTO.getName()).addResource(ResourceType.WOOD,
                    playerDTO.getWood());
            getPlayer(playerDTO.getName()).addResource(ResourceType.STONE,
                    playerDTO.getStone());
        }

    }

    public Collection<Player> players() {
        return players.values();
    }
}
