package pl.rembol.jme3.game;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import pl.rembol.jme3.game.events.EventManager;
import pl.rembol.jme3.game.gui.window.WindowManager;
import pl.rembol.jme3.game.input.InputListener;
import pl.rembol.jme3.game.input.KeyInputManager;
import pl.rembol.jme3.game.input.MouseInputManager;
import pl.rembol.jme3.rts.AssetManagerWrapper;
import pl.rembol.jme3.threads.ThreadManager;

public class GenericGameState {

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
    public final WindowManager windowManager;

    public final InputListener defaultInputListener;

    protected boolean grabCursorWhenNoWindowsOpened = false;

    public GenericGameState(SimpleApplication simpleApplication, AppSettings settings) {
        this.simpleApplication = simpleApplication;
        this.settings = settings;
        this.bulletAppState = new BulletAppState();
        simpleApplication.getStateManager().attach(bulletAppState);

        simpleApplication.getStateManager().attach(new GameCleanupAppState(this));

        assetManager = assetManager(simpleApplication);
        rootNode = simpleApplication.getRootNode();
        guiNode = simpleApplication.getGuiNode();
        camera = simpleApplication.getCamera();
        inputManager = simpleApplication.getInputManager();

        new KeyInputManager(this);
        new MouseInputManager(this);

        defaultInputListener = createInputListener();

        this.windowManager = new WindowManager(this);
    }

    protected AssetManager assetManager(SimpleApplication simpleApplication) {
        return simpleApplication.getAssetManager();
    }

    protected InputListener createInputListener() {
        return new InputListener<GenericGameState>(this) {
        };
    }

    public boolean isGrabCursorWhenNoWindowsOpened() {
        return grabCursorWhenNoWindowsOpened;
    }

}
