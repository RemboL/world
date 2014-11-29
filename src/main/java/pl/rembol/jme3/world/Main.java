package pl.rembol.jme3.world;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.hud.Cursor;
import pl.rembol.jme3.world.interfaces.Tickable;
import pl.rembol.jme3.world.smallobject.Axe;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.scene.plugins.blender.BlenderModelLoader;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {

	public static void main(String[] args) {
		Main app = new Main();
		app.setShowSettings(false);
		AppSettings settings = new AppSettings(true);
		settings.put("Width", 800);
		settings.put("Height", 600);
		settings.put("Title", "My awesome Game");
		settings.put("VSync", true);
		settings.put("Samples", 4);
		app.setSettings(settings);

		app.start();
	}

	private List<Tickable> tickables = new ArrayList<>();

	private BulletAppState bulletAppState;

	private DirectionalLight directional;

	private boolean nightDayEffect = false;

	@Override
	public void simpleInitApp() {

		assetManager.registerLoader(BlenderModelLoader.class, "blend");

		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		// bulletAppState.getPhysicsSpace().enableDebug(assetManager);

		GameState.get().setBulletAppState(bulletAppState);
		GameState.get().setAssetManager(assetManager);
		GameState.get().setRootNode(rootNode);
		GameState.get().setSettings(settings);

		new Cursor(this.getGuiNode());

		 stateManager.attach(new VideoRecorderAppState(new File("video.avi"),
		 0.9f));

		initLightAndShadows();

		flyCam.setMoveSpeed(50);
		cam.setLocation(new Vector3f(0f, 20f, -70f));
		cam.setRotation(new Quaternion().fromAngleAxis(0, Vector3f.UNIT_Y));

		Terrain terrain = new Terrain(cam, 128);
		GameState.get().setTerrain(terrain);

		for (int i = 0; i < 5; ++i) {
			BallMan ballMan = new BallMan(new Vector2f(
					(new Random().nextFloat() + 2f) * 5f,
					(new Random().nextFloat() + i - 3f) * 10f));

			ballMan.wield(new Axe());

			new Tree(new Vector2f((new Random().nextFloat() - 2f) * 5f,
					(new Random().nextFloat() + i - 3f) * 10f));

			tickables.add(ballMan);
		}

		initKeys();

	}

	private void initLightAndShadows() {
		directional = new DirectionalLight();
		directional.setDirection(new Vector3f(-0f, -1f, 0f).normalize());
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

	static int frame = 200;

	private void initKeys() {
		inputManager.addMapping("select", new MouseButtonTrigger(
				MouseInput.BUTTON_LEFT));
		inputManager.addListener(new LeftClickListener(getCamera()), "select");

		inputManager.addMapping("defaultAction", new MouseButtonTrigger(
				MouseInput.BUTTON_RIGHT));
		inputManager.addListener(new RightClickListener(getCamera()),
				"defaultAction");

		inputManager.addMapping("move", new KeyTrigger(KeyInput.KEY_M));
		inputManager.addListener(new CommandKeysListener(), "move");

		inputManager.addMapping("flatten", new KeyTrigger(KeyInput.KEY_F));
		inputManager.addListener(new CommandKeysListener(), "flatten");

	}

	@Override
	public void update() {
		super.update();

		frame++;

		if (nightDayEffect) {
			directional.setDirection(new Vector3f(FastMath.sin(FastMath.TWO_PI
					/ 400 * frame),
					FastMath.sin(FastMath.TWO_PI / 400 * frame) - 0.5f,
					FastMath.cos(FastMath.TWO_PI / 400 * frame)).normalize());
		}

		for (Tickable tickable : tickables) {
			tickable.tick();
		}

		GameState.get().checkForSpatials();

	}
}