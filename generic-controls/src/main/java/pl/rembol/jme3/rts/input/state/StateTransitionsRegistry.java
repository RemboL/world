package pl.rembol.jme3.rts.input.state;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.order.OrderProducer;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.rts.player.WithOwner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StateTransitionsRegistry {

    private final List<StateTransition> transitions = new ArrayList<>();

    private final GameState gameState;

    public StateTransitionsRegistry(GameState gameState) {
        this.gameState = gameState;

    }

    public void register(InputState startingState, Command commandButton, InputState finishState, OrderProducer orderProducer) {
        transitions.add(new StateTransition(startingState, commandButton, finishState, orderProducer));
        gameState.commandKeysListener.bindCommandKey(commandButton.getKey());
    }

    public Optional<StateTransition> match(InputState state,
                                           List<Selectable> selectableList, int key) {
        return transitions.stream()
                .filter(allAreOwnedByActivePlayer(selectableList))
                .filter(allHaveTheCommandAvailable(selectableList))
                .filter(transition -> transition.match(state, key))
                .findFirst();
    }


    public List<StateTransition> match(InputState state, List<Selectable> selectableList) {
        return transitions.stream()
                .filter(allAreOwnedByActivePlayer(selectableList))
                .filter(allHaveTheCommandAvailable(selectableList))
                .filter(transition -> transition.match(state))
                .collect(Collectors.toList());
    }

    private Predicate<StateTransition> allHaveTheCommandAvailable(List<Selectable> selectableList) {
        return entry -> selectableList
                .stream()
                .allMatch(selectable -> selectable
                        .getAvailableOrders()
                        .contains(entry.getCommandButton().getIconName()));
    }

    private Predicate<StateTransition> allAreOwnedByActivePlayer(List<Selectable> selectableList) {
        return entry -> selectableList
                .stream()
                .filter(WithOwner.class::isInstance)
                .map(WithOwner.class::cast)
                .map(WithOwner::getOwner)
                .allMatch(withOwner -> gameState.playerService.getActivePlayer().equals(withOwner));
    }

}
