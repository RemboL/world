package pl.rembol.jme3.rts;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import pl.rembol.jme3.rts.events.EventManager;
import pl.rembol.jme3.rts.gameobjects.order.MoveOrder;
import pl.rembol.jme3.rts.gui.ActionBox;
import pl.rembol.jme3.rts.gui.ResourcesBar;
import pl.rembol.jme3.rts.gui.SelectionBox;
import pl.rembol.jme3.rts.gui.console.ConsoleLog;
import pl.rembol.jme3.rts.gui.status.StatusBar;
import pl.rembol.jme3.rts.input.CommandKeysListener;
import pl.rembol.jme3.rts.input.ModifierKeysManager;
import pl.rembol.jme3.rts.input.MouseClickListener;
import pl.rembol.jme3.rts.input.RtsCamera;
import pl.rembol.jme3.rts.input.dragselect.DragSelectionManager;
import pl.rembol.jme3.rts.input.state.*;
import pl.rembol.jme3.rts.pathfinding.PathfindingService;
import pl.rembol.jme3.rts.player.PlayerService;
import pl.rembol.jme3.rts.resources.ResourceType;
import pl.rembol.jme3.rts.terrain.Terrain;
import pl.rembol.jme3.rts.threads.ThreadManager;
import pl.rembol.jme3.rts.unitregistry.UnitRegistry;

import java.util.List;

import static pl.rembol.jme3.rts.input.state.ActionButtonPosition.LOWER_RIGHT;
import static pl.rembol.jme3.rts.input.state.ActionButtonPosition.UPPER_LEFT;
import static pl.rembol.jme3.rts.input.state.InputState.*;

public class GameState {

    public final SimpleApplication simpleApplication;
    public final AppSettings settings;
    public final BulletAppState bulletAppState;

    public final AssetManager assetManager;
    public final Node rootNode;
    public final Node guiNode;
    public final Camera camera;
    public final InputManager inputManager;

    public final ThreadManager threadManager = new ThreadManager();
    public final EventManager eventManager = new EventManager();

    public final PathfindingService pathfindingService;
    public final Terrain terrain;
    public final ResourcesBar resourcesBar;
    public final ConsoleLog consoleLog;
    public final ModifierKeysManager modifierKeysManager;
    public final StatusBar statusBar;
    public final SelectionBox selectionBox;

    public final PlayerService playerService;
    public final UnitRegistry unitRegistry;
    public final SelectionManager selectionManager;
    public final BuildingSilhouetteManager buildingSilhouetteManager;
    public final ActionBox actionBox;
    public final InputStateManager inputStateManager;
    public final CommandKeysListener commandKeysListener;
    public final StateTransitionsRegistry stateTransitionsRegistry;
    public final DragSelectionManager dragSelectionManager;
    public final MouseClickListener mouseClickListener;

    public GameState(SimpleApplication simpleApplication, AppSettings settings, BulletAppState bulletAppState) {
        this.simpleApplication = simpleApplication;
        this.settings = settings;
        this.bulletAppState = bulletAppState;

        assetManager = simpleApplication.getAssetManager();
        rootNode = simpleApplication.getRootNode();
        guiNode = simpleApplication.getGuiNode();
        camera = simpleApplication.getCamera();
        inputManager = simpleApplication.getInputManager();

        terrain = new Terrain(simpleApplication, bulletAppState);
        pathfindingService = new PathfindingService(terrain, threadManager);

        resourcesBar = new ResourcesBar(simpleApplication, settings);
        consoleLog = new ConsoleLog(simpleApplication, settings);
        modifierKeysManager = new ModifierKeysManager(simpleApplication);
        statusBar = new StatusBar(simpleApplication, settings, eventManager);
        selectionBox = new SelectionBox(simpleApplication);
        new RtsCamera(simpleApplication);

        playerService = new PlayerService(resourcesBar, consoleLog);
        unitRegistry = new UnitRegistry(this);
        selectionManager = new SelectionManager(this);
        buildingSilhouetteManager = new BuildingSilhouetteManager(this);
        actionBox = new ActionBox(this);
        inputStateManager = new InputStateManager(this);
        commandKeysListener = new CommandKeysListener(this);
        stateTransitionsRegistry = new StateTransitionsRegistry(this);
        dragSelectionManager = new DragSelectionManager(this);
        mouseClickListener = new MouseClickListener(this);

        initStateTransitions();
    }

    protected void initResources(List<ResourceType> resourceTypeList) {
        resourcesBar.init(resourceTypeList);
    }

    protected void initStateTransitions() {
        stateTransitionsRegistry.register(DEFAULT, new Command(UPPER_LEFT, "move", KeyInput.KEY_M), ISSUE_ORDER, MoveOrder::new);

        stateTransitionsRegistry.register(ISSUE_ORDER, new Command(LOWER_RIGHT, "cancel", KeyInput.KEY_C), DEFAULT, null);
        stateTransitionsRegistry.register(BUILD_MENU, new Command(LOWER_RIGHT, "cancel", KeyInput.KEY_C), DEFAULT, null);
        stateTransitionsRegistry.register(ISSUE_BUILD_ORDER, new Command(LOWER_RIGHT, "cancel", KeyInput.KEY_C), DEFAULT, null);
    }

    protected void initGuiFrames(String themeName) {
        resourcesBar.initFrame(themeName);
        selectionBox.initFrame(themeName);
        actionBox.initFrame(themeName);
        statusBar.initFrame(themeName);
    }
}
