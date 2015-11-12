package pl.rembol.jme3.rts;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import pl.rembol.jme3.rts.events.EventManager;
import pl.rembol.jme3.rts.gui.ResourcesBar;
import pl.rembol.jme3.rts.gui.SelectionBox;
import pl.rembol.jme3.rts.gui.console.ConsoleLog;
import pl.rembol.jme3.rts.gui.status.StatusBar;
import pl.rembol.jme3.rts.input.ModifierKeysManager;
import pl.rembol.jme3.rts.input.RtsCamera;
import pl.rembol.jme3.rts.input.state.BuildingSilhouetteManager;
import pl.rembol.jme3.rts.input.state.SelectionManager;
import pl.rembol.jme3.rts.pathfinding.PathfindingService;
import pl.rembol.jme3.rts.player.PlayerService;
import pl.rembol.jme3.rts.resources.ResourceType;
import pl.rembol.jme3.rts.terrain.Terrain;
import pl.rembol.jme3.rts.threads.ThreadManager;
import pl.rembol.jme3.rts.unitregistry.UnitRegistry;

import java.util.List;

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

    public final PlayerService playerService;
    public final UnitRegistry unitRegistry;
    public final SelectionManager selectionManager;
    public final BuildingSilhouetteManager buildingSilhouetteManager;


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
        new SelectionBox(simpleApplication);
        new RtsCamera(simpleApplication);

        playerService = new PlayerService(resourcesBar, consoleLog);
        unitRegistry = new UnitRegistry(this);
        selectionManager = new SelectionManager(this);
        buildingSilhouetteManager = new BuildingSilhouetteManager(this);
    }

    protected void initResources(List<ResourceType> resourceTypeList) {
        resourcesBar.init(resourceTypeList);
    }

    protected void initStateTransitions() {

    }
}
