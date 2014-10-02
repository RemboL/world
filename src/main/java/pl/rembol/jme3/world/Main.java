package pl.rembol.jme3.world;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.smallobject.Axe;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.plugins.blender.BlenderModelLoader;
import com.jme3.shadow.DirectionalLightShadowFilter;
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

	private List<BallMan> ballMen = new ArrayList<>();

	private BulletAppState bulletAppState;

	private DirectionalLight directional;

	private boolean nightDayEffect = false;

	@Override
	public void simpleInitApp() {

		assetManager.registerLoader(BlenderModelLoader.class, "blend");

		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);

		GameState.setBulletAppState(bulletAppState);
		GameState.setAssetManager(assetManager);
		GameState.setRootNode(rootNode);

		stateManager.attach(new VideoRecorderAppState(new File("video.avi"),
				0.9f));

		initLightAndShadows();

		flyCam.setMoveSpeed(50);
		cam.setLocation(new Vector3f(0f, -80f, -70f));
		cam.setRotation(new Quaternion().fromAngleAxis(0, Vector3f.UNIT_Y));

		Terrain terrain = new Terrain(cam, 128);
		GameState.setTerrain(terrain);

		for (int i = 0; i < 10; ++i) {
			BallMan ballMan = new BallMan(new Vector3f(
					(new Random().nextFloat() + 1f) * 25f, -40f
							+ (new Random().nextFloat() * 10f),
					(new Random().nextFloat() - .5f) * 50f));
			ballMen.add(ballMan);
			ballMan.wield(new Axe());

			Vector3f position = new Vector3f(
					(new Random().nextFloat() - 2f) * 25f, -40f
							+ (new Random().nextFloat() * 10f),
					(new Random().nextFloat() - .5f) * 100f);

			Tree tree = new Tree(position);

			ballMan.attack(tree);

		}

	}

	private void initLightAndShadows() {
		directional = new DirectionalLight();
		directional.setDirection(new Vector3f(-0f, -1f, 0f).normalize());
		directional.setColor(ColorRGBA.White.mult(1f));
		rootNode.addLight(directional);
		AmbientLight ambient = new AmbientLight();
		ambient.setColor(ColorRGBA.White.mult(0.5f));
		rootNode.addLight(ambient);

		final int SHADOWMAP_SIZE = 1024;
		DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(
				assetManager, SHADOWMAP_SIZE, 3);
		dlsr.setLight(directional);
		viewPort.addProcessor(dlsr);

		DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(
				assetManager, SHADOWMAP_SIZE, 3);
		dlsf.setEnabled(true);

		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
		fpp.addFilter(dlsf);

		viewPort.addProcessor(fpp);
	}

	static int frame = 200;

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

		for (BallMan ballMan : ballMen) {
			ballMan.tick();
		}

	}
}