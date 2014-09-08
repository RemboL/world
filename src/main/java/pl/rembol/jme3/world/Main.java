package pl.rembol.jme3.world;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {

	public static void main(String[] args) {
		Main app = new Main();
		app.setShowSettings(false);
		AppSettings settings = new AppSettings(true);
		settings.put("Width", 1280);
		settings.put("Height", 720);
		settings.put("Title", "My awesome Game");
		settings.put("VSync", true);
		settings.put("Samples", 4);
		app.setSettings(settings);

		app.start();
	}

	private List<BallMan> ballMen = new ArrayList<>();

	private BulletAppState bulletAppState;

	@Override
	public void simpleInitApp() {

		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);

		initLightAndShadows();

		flyCam.setMoveSpeed(50);
		cam.setLocation(new Vector3f(0f, -40f, -70f));
		cam.setRotation(new Quaternion().fromAngleAxis(0, Vector3f.UNIT_Y));

		Terrain terrain = new Terrain(assetManager, rootNode, cam, 128,
				bulletAppState);

		for (int i = 0; i < 20; ++i) {
			BallMan ballMan = new BallMan(new Vector3f(
					(new Random().nextFloat() - .5f) * 50f, -40f
							+ (new Random().nextFloat() * 10f),
					(new Random().nextFloat() - .5f) * 50f), rootNode,
					assetManager, bulletAppState);
			ballMen.add(ballMan);
		}

		// inputManager.addMapping("Shoot", new MouseButtonTrigger(
		// MouseInput.BUTTON_LEFT));
		// inputManager.addListener(new ShootListener(cam, terrain), "Shoot");
	}

	private void initLightAndShadows() {
		DirectionalLight directional = new DirectionalLight();
		directional.setDirection(new Vector3f(0f, -1f, 0f).normalize());
		rootNode.addLight(directional);
		AmbientLight ambient = new AmbientLight();
		ambient.setColor(ColorRGBA.White.mult(1f));
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

	@Override
	public void update() {
		super.update();
		for (BallMan ballMan : ballMen) {
			ballMan.tick();
		}

	}
}