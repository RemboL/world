package pl.rembol.jme3.world;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import pl.rembol.jme3.rts.terrain.terrain.Terrain;
import pl.rembol.jme3.world.ballman.order.OrderFactory;
import pl.rembol.jme3.world.hud.ActionBox;
import pl.rembol.jme3.world.hud.ConsoleLog;
import pl.rembol.jme3.world.hud.ResourcesBar;
import pl.rembol.jme3.world.hud.SelectionBox;
import pl.rembol.jme3.world.hud.status.StatusBar;
import pl.rembol.jme3.world.input.*;
import pl.rembol.jme3.world.input.state.BuildingSilhouetteManager;
import pl.rembol.jme3.world.input.state.InputStateManager;
import pl.rembol.jme3.world.input.state.SelectionManager;
import pl.rembol.jme3.world.pathfinding.PathfindingService;
import pl.rembol.jme3.world.player.PlayerService;
import pl.rembol.jme3.world.threads.ThreadManager;

public class GameState {

    public final AssetManager assetManager;

    public final Node rootNode;

    public final Node guiNode;

    public final Camera camera;

    public final SimpleApplication simpleApplication;

    public final AppSettings settings;

    public final BulletAppState bulletAppState;

    public final InputManager inputManager;

    public final ThreadManager threadManager = new ThreadManager();

    public final PathfindingService pathfindingService;

    public final Terrain terrain;

    public final ResourcesBar resourcesBar;

    public final ConsoleLog consoleLog;

    public final ModifierKeysManager modifierKeysManager;

    public final StatusBar statusBar;

    public final UnitRegistry unitRegistry;

    public final SelectionManager selectionManager;

    public final PlayerService playerService;

    public final BuildingSilhouetteManager buildingSilhouetteManager;

    public final OrderFactory orderFactory;

    public final ActionBox actionBox;

    public final InputStateManager inputStateManager;

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

        pathfindingService = new PathfindingService(this);
        terrain = new Terrain(assetManager, bulletAppState);
        resourcesBar = new ResourcesBar(this);
        consoleLog = new ConsoleLog(this);
        modifierKeysManager = new ModifierKeysManager(this);
        statusBar = new StatusBar(this);
        new SelectionBox(this);
        unitRegistry = new UnitRegistry(this);
        selectionManager = new SelectionManager(this);
        playerService = new PlayerService(this);
        buildingSilhouetteManager = new BuildingSilhouetteManager(this);
        orderFactory = new OrderFactory(this);
        actionBox = new ActionBox(this);
        inputStateManager = new InputStateManager(this);
        new CommandKeysListener(this);
        dragSelectionManager = new DragSelectionManager(this);
        mouseClickListener = new MouseClickListener(this);
        new RtsCamera(this);
    }

}
