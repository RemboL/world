package pl.rembol.jme3.world;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.plugins.blender.BlenderModelLoader;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {

    public Main(AppState[] appStates) {
        super(appStates);
    }

    public static void main(String[] args) {
        Main app = new Main(new AppState[]{});
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
        setDisplayStatView(false);
        setDisplayFps(false);

        assetManager.registerLoader(BlenderModelLoader.class, "blend");

//		 stateManager.attach(new VideoRecorderAppState(new File("video.avi"),
//		 0.7f));


        cam.setLocation(new Vector3f(0f, 20f, -20f));
        cam.setRotation(new Quaternion().fromAngleAxis(0, Vector3f.UNIT_Y));

        this.getStateManager().attach(new GameRunningAppState(settings));

    }

}