package pl.rembol.jme3.world;

import java.util.Random;

import pl.rembol.jme3.input.CommandKeysListener;
import pl.rembol.jme3.input.ModifierKeysManager;
import pl.rembol.jme3.input.MouseClickListener;
import pl.rembol.jme3.input.RtsCamera;
import pl.rembol.jme3.input.state.InputStateManager;
import pl.rembol.jme3.input.state.SelectionManager;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.order.OrderFactory;
import pl.rembol.jme3.world.hud.HudManager;
import pl.rembol.jme3.world.smallobject.Axe;
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
import com.jme3.terrain.geomipmap.TerrainQuad;

public class GameRunningAppState extends AbstractAppState {

	private BulletAppState bulletAppState;

	private DirectionalLight directional;

	private boolean nightDayEffect = false;

	private AssetManager assetManager;

	private Node rootNode;

	private Node guiNode;

	private Camera camera;

	private RtsCamera rtsCamera;

	int frame = 200;

	private Terrain terrain;

	private OrderFactory orderFactory;

	private InputStateManager inputStateManager;

	private ModifierKeysManager modifierKeysManager;

	private SelectionManager selectionManager;

	private HudManager hudManager;

	private AppSettings settings;

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
		// bulletAppState.getPhysicsSpace().enableDebug(assetManager);

		this.assetManager = app.getAssetManager();

		this.rootNode = simpleApp.getRootNode();

		this.guiNode = simpleApp.getGuiNode();

		this.camera = app.getCamera();

		this.hudManager = new HudManager(guiNode, settings, assetManager);

		this.rtsCamera = new RtsCamera(camera);

		this.orderFactory = new OrderFactory(this);

		this.inputStateManager = new InputStateManager(this, orderFactory);

		this.selectionManager = new SelectionManager(this);
		GameState.get().setSelectionManager(selectionManager);

		terrain = new Terrain(camera, 128, this);
		GameState.get().setTerrain(terrain);

		initLightAndShadows(app.getViewPort());

		for (int i = 0; i < 5; ++i) {
			BallMan ballMan = new BallMan(new Vector2f(
					(new Random().nextFloat() + 2f) * 5f,
					(new Random().nextFloat() + i - 3f) * 10f), this);

			ballMan.wield(new Axe(this));

			new Tree(new Vector2f((new Random().nextFloat() - 2f) * 5f,
					(new Random().nextFloat() + i - 3f) * 10f), this);

		}

		initKeys(app.getInputManager(), guiNode);
	}

	private void initKeys(InputManager inputManager, Node guiNode) {
		MouseClickListener mouseClickListener = new MouseClickListener(camera,
				guiNode, inputManager, inputStateManager);
		CommandKeysListener commandKeysListener = new CommandKeysListener(
				inputStateManager);

		mouseClickListener.registerInput();
		commandKeysListener.registerInput(inputManager);
		modifierKeysManager = new ModifierKeysManager();
		modifierKeysManager.registerInput(inputManager);

		rtsCamera.registerInput(inputManager);

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
		fog.setFogDistance(200f);
		fog.setFogDensity(1.5f);
		fpp.addFilter(fog);
		viewPort.addProcessor(fpp);
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public BulletAppState getBulletAppState() {
		return bulletAppState;
	}

	public Node getRootNode() {
		return rootNode;
	}

	public TerrainQuad getTerrainQuad() {
		return terrain.getTerrain();
	}

	public Terrain getTerrain() {
		return terrain;
	}

	public ModifierKeysManager getModifierKeysManager() {
		return modifierKeysManager;
	}

	public SelectionManager getSelectionManager() {
		return selectionManager;
	}

	public HudManager getHudManager() {
		return hudManager;
	}

}
