package pl.rembol.jme3.world.input.state;

import pl.rembol.jme3.world.input.state.SelectionManager.SelectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.rembol.jme3.world.ballman.order.OrderFactory.*;
import static pl.rembol.jme3.world.input.state.InputState.*;
import static pl.rembol.jme3.world.input.state.SelectionManager.SelectionType.HOUSE;
import static pl.rembol.jme3.world.input.state.SelectionManager.SelectionType.UNIT;

public class StateTransitions {

    private static final List<StateTransition> transitions = new ArrayList<>();

    static {
        register(DEFAULT, UNIT, Command.MOVE, ISSUE_ORDER, ORDER_MOVE);
        register(DEFAULT, UNIT, Command.FLATTEN, ISSUE_ORDER, ORDER_FLATTEN);
        register(DEFAULT, UNIT, Command.BUILD, BUILD_MENU, null);

        register(BUILD_MENU, UNIT, Command.BUILD_HOUSE, ISSUE_BUILD_ORDER,
                ORDER_BUILD_HOUSE);
        register(BUILD_MENU, UNIT, Command.BUILD_TOOLSHOP, ISSUE_BUILD_ORDER,
                ORDER_BUILD_TOOLSHOP);
        register(BUILD_MENU, UNIT, Command.BUILD_WAREHOUSE, ISSUE_BUILD_ORDER,
                ORDER_BUILD_WAREHOUSE);

        register(DEFAULT, HOUSE, Command.RECRUIT, ISSUE_ORDER_IMMEDIATELY,
                ORDER_RECRUIT);

        register(ISSUE_ORDER, UNIT, Command.CANCEL, DEFAULT, null);
        register(BUILD_MENU, UNIT, Command.CANCEL, DEFAULT, null);
        register(ISSUE_BUILD_ORDER, UNIT, Command.CANCEL, DEFAULT, null);
    }

    private static void register(InputState startingState, SelectionType type,
            Command commandButton, InputState finishState, String order) {
        transitions.add(new StateTransition(startingState, type, commandButton
                .getCommandKey(), commandButton, finishState, order));
    }

    public Optional<StateTransition> match(InputState state,
            SelectionType type, String command) {
        return transitions.stream()
                .filter(transition -> transition.match(state, type, command))
                .findFirst();
    }

    public List<StateTransition> match(InputState state, SelectionType type) {
        return transitions.stream()
                .filter(transition -> transition.match(state, type))
                .collect(Collectors.toList());
    }

}
