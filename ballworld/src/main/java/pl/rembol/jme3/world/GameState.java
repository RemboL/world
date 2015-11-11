package pl.rembol.jme3.world;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.system.AppSettings;
import pl.rembol.jme3.world.hud.ActionBox;
import pl.rembol.jme3.world.input.CommandKeysListener;
import pl.rembol.jme3.world.input.DragSelectionManager;
import pl.rembol.jme3.world.input.MouseClickListener;
import pl.rembol.jme3.world.input.state.BuildingSilhouetteManager;
import pl.rembol.jme3.world.input.state.InputStateManager;
import pl.rembol.jme3.world.input.state.SelectionManager;
import pl.rembol.jme3.world.input.state.StateTransitions;
import pl.rembol.jme3.world.resources.ResourceTypes;

public class GameState extends pl.rembol.jme3.rts.GameState {
    
    public final SelectionManager selectionManager;

    public final BuildingSilhouetteManager buildingSilhouetteManager;

    public final ActionBox actionBox;

    public final InputStateManager inputStateManager;

    public final DragSelectionManager dragSelectionManager;

    public final MouseClickListener mouseClickListener;

    public final BallManUnitRegistry ballManUnitRegistry;

    public final StateTransitions stateTransitions;

    public GameState(SimpleApplication simpleApplication, AppSettings settings, BulletAppState bulletAppState) {
        super(simpleApplication, settings, bulletAppState);


        actionBox = new ActionBox(this);

        selectionManager = new SelectionManager(this);
        buildingSilhouetteManager = new BuildingSilhouetteManager(this);
        inputStateManager = new InputStateManager(this);
        new CommandKeysListener(this);
        dragSelectionManager = new DragSelectionManager(this);
        mouseClickListener = new MouseClickListener(this);

        ballManUnitRegistry = new BallManUnitRegistry(this);

        stateTransitions = new StateTransitions(this);
        initResources(ResourceTypes.values());
        playerService.setResourceTypeList(ResourceTypes.values());
    }

}
