package pl.rembol.jme3.world.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import pl.rembol.jme3.world.resources.ResourceType;
import pl.rembol.jme3.world.save.PlayerDTO;

import com.jme3.math.ColorRGBA;

@Component
public class PlayerService implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private Map<String, Player> players = new HashMap<>();

    private Player activePlayer;

    public void registerPlayer(String name, ColorRGBA color, boolean active) {

        if (players.containsKey(name)) {
            System.out.println("player with name \"" + name
                    + "\" already registered.");
            return;
        }

        Player player = applicationContext.getAutowireCapableBeanFactory()
                .createBean(Player.class);
        player.setName(name);
        player.setColor(color);
        player.setActive(active);
        if (active) {
            activePlayer = player;
        }

        players.put(player.getName(), player);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Player getPlayer(String name) {
        return players.get(name);
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public List<PlayerDTO> savePlayers() {
        return players.values().stream().map(player -> new PlayerDTO(player))
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
}
