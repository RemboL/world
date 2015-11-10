package pl.rembol.jme3.world.input.state;

import static pl.rembol.jme3.world.input.state.InputState.BUILD_MENU;
import static pl.rembol.jme3.world.input.state.InputState.DEFAULT;
import static pl.rembol.jme3.world.input.state.InputState.ISSUE_BUILD_ORDER;
import static pl.rembol.jme3.world.input.state.InputState.ISSUE_ORDER;
import static pl.rembol.jme3.world.input.state.InputState.ISSUE_ORDER_IMMEDIATELY;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import pl.rembol.jme3.rts.player.WithOwner;
import pl.rembol.jme3.rts.unit.selection.Selectable;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.order.BuildHouseOrder;
import pl.rembol.jme3.world.ballman.order.BuildToolshopOrder;
import pl.rembol.jme3.world.ballman.order.BuildWarehouseOrder;
import pl.rembol.jme3.world.ballman.order.MoveOrder;
import pl.rembol.jme3.world.ballman.order.OrderProducer;
import pl.rembol.jme3.world.ballman.order.RecruitOrder;
import pl.rembol.jme3.world.ballman.order.SmoothenTerrainOrder;

public class StateTransitions {

    private final List<StateTransition> transitions = new ArrayList<>();
    
    private final GameState gameState;
    
    public StateTransitions(GameState gameState) {
        this.gameState = gameState;

        register(DEFAULT, Command.MOVE, ISSUE_ORDER, MoveOrder::new);
        register(DEFAULT, Command.FLATTEN, ISSUE_ORDER, SmoothenTerrainOrder::new);
        register(DEFAULT, Command.BUILD, BUILD_MENU, null);

        register(BUILD_MENU, Command.BUILD_HOUSE, ISSUE_BUILD_ORDER, BuildHouseOrder::new);
        register(BUILD_MENU, Command.BUILD_TOOLSHOP, ISSUE_BUILD_ORDER, BuildToolshopOrder::new);
        register(BUILD_MENU, Command.BUILD_WAREHOUSE, ISSUE_BUILD_ORDER, BuildWarehouseOrder::new);

        register(DEFAULT, Command.RECRUIT, ISSUE_ORDER_IMMEDIATELY, RecruitOrder::new);

        register(ISSUE_ORDER, Command.CANCEL, DEFAULT, null);
        register(BUILD_MENU, Command.CANCEL, DEFAULT, null);
        register(ISSUE_BUILD_ORDER, Command.CANCEL, DEFAULT, null);
    }

    private void register(InputState startingState,
                                 Command commandButton, InputState finishState, OrderProducer orderProducer) {
        transitions.add(new StateTransition(startingState, commandButton
                .getCommandKey(), commandButton, finishState, orderProducer));
    }

    public Optional<StateTransition> match(InputState state,
                                           List<Selectable> selectableList, String command) {
        return transitions.stream()
                .filter(allAreOwnedByActivePlayer(selectableList))
                .filter(allHaveTheCommandAvailable(selectableList))
                .filter(transition -> transition.match(state, command))
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
