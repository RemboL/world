package pl.rembol.jme3.world;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.house.House;
import pl.rembol.jme3.world.player.Player;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;

public class GameRunningAppState extends AbstractAppState {

	private BulletAppState bulletAppState;

	private DirectionalLight directional;

	private boolean nightDayEffect = false;

	private AssetManager assetManager;

	private Node rootNode;

	int frame = 200;

	private Terrain terrain;

	private AppSettings settings;

	private Player activePlayer;

	private ApplicationContext applicationContext;

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
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		SimpleApplication simpleApp = (SimpleApplication) app;

		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		// bulletAppState.setDebugEnabled(true);

		ConfigurableApplicationContext parentApplicationContext = initializeParentApplicationContext(simpleApp);

		applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "/app-ctx.xml" }, parentApplicationContext);

		terrain = applicationContext.getBean(Terrain.class);
		terrain.init(128);

		this.assetManager = app.getAssetManager();
		this.rootNode = simpleApp.getRootNode();

		initLightAndShadows(app.getViewPort());

		activePlayer = applicationContext.getAutowireCapableBeanFactory()
				.createBean(Player.class);
		activePlayer.setName("RemboL");
		activePlayer.setColor(ColorRGBA.Yellow);
		activePlayer.setActive(true);
		activePlayer.addWood(500);

		BallMan ballMan = new BallMan(applicationContext, new Vector2f(12.5f,
				30f));
		ballMan.setOwner(activePlayer);
		BallMan ballMan2 = new BallMan(applicationContext, new Vector2f(32.5f,
				30f));
		ballMan2.setOwner(activePlayer);

		createHouse(2, 4);
		createHouse(3, 3);
		createHouse(2, 2);
		createHouse(4, 1);
		createHouse(4, 0);
		createHouse(3, -1);
		createHouse(3, -2);
		createHouse(3, -3);
		createHouse(5, -2);
		createHouse(6, -2);
		createHouse(7, -2);
		createHouse(8, -3);
		createHouse(4, -4);
		createHouse(5, -4);
		createHouse(6, -4);
		createHouse(8, -4);
		createHouse(6, -5);
		createHouse(8, -5);
		createHouse(7, -6);
		
	}

	private void createHouse(int x, int y) {
		Building house = applicationContext.getAutowireCapableBeanFactory()
				.createBean(House.class);
		house.init(new Vector2f(10 * x, 10 * y));
		house.setOwner(activePlayer);
	}

	private ConfigurableApplicationContext initializeParentApplicationContext(
			SimpleApplication simpleApp) {
		ConfigurableApplicationContext parentApplicationContext = new GenericApplicationContext();

		parentApplicationContext.getBeanFactory()
				.registerSingleton(AssetManager.class.getSimpleName(),
						simpleApp.getAssetManager());
		parentApplicationContext.getBeanFactory().registerSingleton(
				GameRunningAppState.class.getSimpleName(), this);
		parentApplicationContext.getBeanFactory().registerSingleton("guiNode",
				simpleApp.getGuiNode());
		parentApplicationContext.getBeanFactory().registerSingleton("rootNode",
				simpleApp.getRootNode());
		parentApplicationContext.getBeanFactory().registerSingleton(
				AppSettings.class.getSimpleName(), settings);
		parentApplicationContext.getBeanFactory().registerSingleton(
				Camera.class.getSimpleName(), simpleApp.getCamera());
		parentApplicationContext.getBeanFactory()
				.registerSingleton(InputManager.class.getSimpleName(),
						simpleApp.getInputManager());
		parentApplicationContext.getBeanFactory().registerSingleton(
				BulletAppState.class.getSimpleName(), bulletAppState);
		parentApplicationContext.getBeanFactory().registerSingleton(
				SimpleApplication.class.getSimpleName(), simpleApp);

		parentApplicationContext.refresh();

		return parentApplicationContext;
	}

	private void initLightAndShadows(ViewPort viewPort) {
		directional = new DirectionalLight();
		directional.setDirection(new Vector3f(-2f, -10f, -5f).normalize());
		directional.setColor(ColorRGBA.White.mult(.7f));
		rootNode.addLight(directional);
		AmbientLight ambient = new AmbientLight();
		ambient.setColor(ColorRGBA.White.mult(0.3f));
		rootNode.addLight(ambient);

		final int SHADOWMAP_SIZE = 1024;
		DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(
				assetManager, SHADOWMAP_SIZE, 3);
		dlsr.setLight(directional);
		viewPort.addProcessor(dlsr);

		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

		FogFilter fog = new FogFilter();
		fog.setFogColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		fog.setFogDistance(400f);
		fog.setFogDensity(1.5f);
		fpp.addFilter(fog);
		viewPort.addProcessor(fpp);
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

}
