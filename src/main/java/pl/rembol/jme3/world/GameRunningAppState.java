package pl.rembol.jme3.world;

import java.util.Random;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import pl.rembol.jme3.input.state.SelectionManager;
import pl.rembol.jme3.player.Player;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.house.House;
import pl.rembol.jme3.world.smallobject.Axe;
import pl.rembol.jme3.world.terrain.Terrain;
import pl.rembol.jme3.world.warehouse.Warehouse;

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

	private SelectionManager selectionManager;

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
		terrain.smoothenTerrain(new Vector2f(25f, -5f), new Vector2f(35f, 5f),
				5, 20f);

		this.assetManager = app.getAssetManager();
		this.rootNode = simpleApp.getRootNode();

		this.selectionManager = applicationContext
				.getBean(SelectionManager.class);

		GameState.get().setSelectionManager(selectionManager);
		initLightAndShadows(app.getViewPort());

		activePlayer = applicationContext.getAutowireCapableBeanFactory()
				.createBean(Player.class);
		activePlayer.setName("RemboL");
		activePlayer.setColor(ColorRGBA.Yellow);
		activePlayer.setActive(true);
		activePlayer.addWood(500);

		Player enemy = applicationContext.getAutowireCapableBeanFactory()
				.createBean(Player.class);
		enemy.setName("bad guy");
		enemy.setColor(ColorRGBA.Red);

		Warehouse warehouse = applicationContext
				.getAutowireCapableBeanFactory().createBean(Warehouse.class)
				.init(new Vector2f(30f, 0f));
		warehouse.setOwner(activePlayer);

		for (int i = 0; i < 5; ++i) {
			BallMan ballMan = new BallMan(applicationContext, new Vector2f(
					(new Random().nextFloat() + 2f) * 5f,
					(new Random().nextFloat() + i - 3f) * 10f));
			ballMan.setOwner(activePlayer);

			ballMan.wield(new Axe(applicationContext));

			applicationContext
					.getAutowireCapableBeanFactory()
					.createBean(Tree.class)
					.init(new Vector2f((new Random().nextFloat() - 2f) * 5f,
							(new Random().nextFloat() + i - 3f) * 10f));

		}

		BallMan ballMan = new BallMan(applicationContext, new Vector2f(50f,
				-10f));
		ballMan.setOwner(enemy);
		terrain.smoothenTerrain(new Vector2f(45f, 5f), new Vector2f(55f, 15f),
				5, 20f);
		House house = new House(applicationContext, new Vector2f(50f, 10f));
		house.setOwner(enemy);

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
