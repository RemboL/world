package pl.rembol.jme3.world;

import java.io.File;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import pl.rembol.jme3.rts.gui.window.Window;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.building.toolshop.Toolshop;
import pl.rembol.jme3.world.resources.ResourceTypes;
import pl.rembol.jme3.world.resources.deposits.FruitBush;
import pl.rembol.jme3.world.save.SaveState;

public class GameRunningAppState extends AbstractAppState {

    private DirectionalLight directional;

    private boolean nightDayEffect = false;

    int frame = 200;

    private AppSettings settings;

    private GameState gameState;

    public GameRunningAppState(AppSettings settings) {
        this.settings = settings;
    }

    @Override
    public void update(float tpf) {
        frame++;

        if (nightDayEffect) {
            directional.setDirection(new Vector3f(FastMath.sin(FastMath.TWO_PI
                    / 400 * frame),
                    FastMath.sin(FastMath.TWO_PI / 400 * frame) - 0.5f,
                    FastMath.cos(FastMath.TWO_PI / 400 * frame)).normalize());
        }

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

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        // bulletAppState.setDebugEnabled(true);

        gameState = new GameState(simpleApp, settings, bulletAppState);

        initLightAndShadows(gameState, app.getViewPort());

        SaveState load = SaveState.load("ballworld" + File.separator + "save.xml");
        gameState.terrain.init(load.getTerrain());
        gameState.pathfindingService.initFromTerrain();

        gameState.playerService.loadPlayers(load.getPlayers());
        gameState.unitRegistry.load(load.getUnits());

        Building toolshop = new Toolshop(gameState).init(new Vector2f(15, -20));
        toolshop.setOwner(gameState.playerService.getActivePlayer());
        gameState.playerService.getActivePlayer().addResource(ResourceTypes.WOOD, 200);

//        new Rabbit(gameState, new Vector2f(30, 0));
//        new Rabbit(gameState, new Vector2f(30, 10));
//        new Rabbit(gameState, new Vector2f(40, 10));
//        new Rabbit(gameState, new Vector2f(40, 0));

        new FruitBush(gameState).init(new Vector2f(35, -20));

        gameState.windowManager.addWindow(new Window(gameState, 640, 480), new Vector2f(100, 100));

//        new SaveState(gameState.terrain.save(), gameState.playerService.savePlayers(),
//                gameState.unitRegistry.save())
//                .save("default.xml");

    }

    private void initLightAndShadows(GameState gameState, ViewPort viewPort) {
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

        FilterPostProcessor fpp = new FilterPostProcessor(gameState.assetManager);

        FogFilter fog = new FogFilter();
        fog.setFogColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        fog.setFogDistance(400f);
        fog.setFogDensity(1.5f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
    }

}
