package pl.rembol.jme3.world.input.state;

import com.jme3.input.KeyInput;
import pl.rembol.jme3.rts.input.state.Command;
import pl.rembol.jme3.rts.input.state.InputState;
import pl.rembol.jme3.rts.input.state.StateTransition;
import pl.rembol.jme3.rts.player.WithOwner;
import pl.rembol.jme3.rts.gameobjects.order.MoveOrder;
import pl.rembol.jme3.rts.gameobjects.order.OrderProducer;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.world.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static pl.rembol.jme3.rts.input.state.ActionButtonPosition.LOWER_RIGHT;
import static pl.rembol.jme3.rts.input.state.ActionButtonPosition.UPPER_LEFT;
import static pl.rembol.jme3.rts.input.state.InputState.*;

public class StateTransitionsRegistry {

    private final List<StateTransition> transitions = new ArrayList<>();

    private final GameState gameState;

    public StateTransitionsRegistry(GameState gameState) {
        this.gameState = gameState;

        register(DEFAULT, new Command(UPPER_LEFT, "move", KeyInput.KEY_M), ISSUE_ORDER, MoveOrder::new);

        register(ISSUE_ORDER, new Command(LOWER_RIGHT, "cancel", KeyInput.KEY_C), DEFAULT, null);
        register(BUILD_MENU, new Command(LOWER_RIGHT, "cancel", KeyInput.KEY_C), DEFAULT, null);
        register(ISSUE_BUILD_ORDER, new Command(LOWER_RIGHT, "cancel", KeyInput.KEY_C), DEFAULT, null);
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
