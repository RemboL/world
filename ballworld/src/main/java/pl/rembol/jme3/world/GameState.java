package pl.rembol.jme3.world;

import static pl.rembol.jme3.rts.input.state.ActionButtonPosition.UPPER_CENTER;
import static pl.rembol.jme3.rts.input.state.ActionButtonPosition.UPPER_LEFT;
import static pl.rembol.jme3.rts.input.state.ActionButtonPosition.UPPER_RIGHT;
import static pl.rembol.jme3.rts.input.state.InputState.BUILD_MENU;
import static pl.rembol.jme3.rts.input.state.InputState.DEFAULT;
import static pl.rembol.jme3.rts.input.state.InputState.ISSUE_BUILD_ORDER;
import static pl.rembol.jme3.rts.input.state.InputState.ISSUE_ORDER;
import static pl.rembol.jme3.rts.input.state.InputState.ISSUE_ORDER_IMMEDIATELY;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.system.AppSettings;
import pl.rembol.jme3.rts.input.state.Command;
import pl.rembol.jme3.world.ballman.order.BuildHouseOrder;
import pl.rembol.jme3.world.ballman.order.BuildToolshopOrder;
import pl.rembol.jme3.world.ballman.order.BuildWarehouseOrder;
import pl.rembol.jme3.world.ballman.order.RecruitOrder;
import pl.rembol.jme3.world.ballman.order.SmoothenTerrainOrder;
import pl.rembol.jme3.world.resources.ResourceTypes;

public class GameState extends pl.rembol.jme3.rts.GameState {

    public GameState(SimpleApplication simpleApplication, AppSettings settings, BulletAppState bulletAppState) {
        super(simpleApplication, settings, bulletAppState);

        initResources(ResourceTypes.values());
        playerService.setResourceTypeList(ResourceTypes.values());

        initGuiFrames("wooden");
    }

    @Override
    protected void initStateTransitions() {
        super.initStateTransitions();

        stateTransitionsRegistry.register(DEFAULT, new Command(UPPER_CENTER, "flatten", KeyInput.KEY_F), ISSUE_ORDER, SmoothenTerrainOrder::new);
        stateTransitionsRegistry.register(DEFAULT, new Command(UPPER_RIGHT, "build", KeyInput.KEY_B), BUILD_MENU, null);

        stateTransitionsRegistry.register(BUILD_MENU, new Command(UPPER_LEFT, "house", KeyInput.KEY_H), ISSUE_BUILD_ORDER, BuildHouseOrder::new);
        stateTransitionsRegistry.register(BUILD_MENU, new Command(UPPER_CENTER, "toolshop", KeyInput.KEY_T), ISSUE_BUILD_ORDER, BuildToolshopOrder::new);
        stateTransitionsRegistry.register(BUILD_MENU, new Command(UPPER_RIGHT, "warehouse", KeyInput.KEY_W), ISSUE_BUILD_ORDER, BuildWarehouseOrder::new);

        stateTransitionsRegistry.register(DEFAULT, new Command(UPPER_LEFT, "ballman", KeyInput.KEY_R), ISSUE_ORDER_IMMEDIATELY, RecruitOrder::new);
    }

}
