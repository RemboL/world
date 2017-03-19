package pl.rembol.jme3.game;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;

public class GameRunningAppState extends AbstractAppState {

    private DirectionalLight directional;

    private AppSettings settings;

    private GenericGameState gameState;

    public GameRunningAppState(AppSettings settings) {
        this.settings = settings;
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void cleanup() {
        if (gameState != null) {
            gameState.threadManager.tearDown();
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        SimpleApplication simpleApp = (SimpleApplication) app;

        // bulletAppState.setDebugEnabled(true);

        gameState = new GenericGameState(simpleApp, settings);

        initLightAndShadows(gameState, app.getViewPort());

    }

    private void initLightAndShadows(GenericGameState gameState, ViewPort viewPort) {
        directional = new DirectionalLight();
        directional.setDirection(new Vector3f(-2f, -10f, -5f).normalize());
        directional.setColor(ColorRGBA.White.mult(.7f));
        gameState.rootNode.addLight(directional);
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.3f));
        gameState.rootNode.addLight(ambient);

        final int SHADOWMAP_SIZE = 1024;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(gameState.assetManager, SHADOWMAP_SIZE,
                3);
        dlsr.setLight(directional);
        viewPort.addProcessor(dlsr);
    }

}
