package pl.rembol.jme3.world;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.system.AppSettings;

// for exercise 2

/**
 * Sample 10 - How to create fast-rendering terrains from heightmaps, and how to
 * use texture splatting to make the terrain look good.
 */
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

	@Override
	public void simpleInitApp() {
		flyCam.setMoveSpeed(50);

		Terrain terrain = null;
		try {
			terrain = new Terrain(assetManager, rootNode, cam, 128);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_SPACE), // trigger
				// 1:
				// spacebar
				new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2:
		// left-button
		// click
		inputManager.addListener(new ShootListener(cam, terrain), "Shoot");
	}

}