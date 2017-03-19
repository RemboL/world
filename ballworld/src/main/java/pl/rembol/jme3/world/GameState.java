package pl.rembol.jme3.world;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.system.AppSettings;
import pl.rembol.jme3.rts.AssetManagerWrapper;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.input.state.Command;
import pl.rembol.jme3.world.ballman.order.*;
import pl.rembol.jme3.world.resources.ResourceTypes;

import static pl.rembol.jme3.rts.input.state.ActionButtonPosition.*;
import static pl.rembol.jme3.rts.input.state.InputState.*;

public class GameState extends RtsGameState {

    public GameState(SimpleApplication simpleApplication, AppSettings settings) {
        super(simpleApplication, settings);

        initResources(ResourceTypes.values());
        playerService.setResourceTypeList(ResourceTypes.values());

        initGuiFrames("wooden");
    }

    @Override
    protected AssetManager assetManager(SimpleApplication simpleApplication) {
        return new AssetManagerWrapper(simpleApplication.getAssetManager());
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
