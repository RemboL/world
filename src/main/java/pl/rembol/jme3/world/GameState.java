package pl.rembol.jme3.world;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

public class GameState {
    
    public final AssetManager assetManager;
    
    public final Node rootNode;
    
    public final Node guiNode;
    
    public final Camera camera;
    
    public final SimpleApplication simpleApplication;

    public final AppSettings settings;

    public final BulletAppState bulletAppState;

    public final InputManager inputManager;

    public GameState(SimpleApplication simpleApplication, AppSettings settings, BulletAppState bulletAppState) {
        this.simpleApplication = simpleApplication;
        this.settings = settings;
        this.bulletAppState = bulletAppState;

        assetManager = simpleApplication.getAssetManager();
        rootNode = simpleApplication.getRootNode();
        guiNode = simpleApplication.getGuiNode();
        camera = simpleApplication.getCamera();
        inputManager = simpleApplication.getInputManager();
    }
    
}
