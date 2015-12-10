package pl.rembol.jme3.rts.gui.model;

import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import pl.rembol.jme3.rts.GameState;

public class ModelViewer {

    private final int width;

    private final int height;

    private final GameState gameState;

    private final ViewPort modelView;

    private final Texture2D offTex;

    private Node modelNode;

    private Node scene;

    Picture viewer;

    public ModelViewer(GameState gameState, int width, int height) {
        this.gameState = gameState;
        this.width = width;
        this.height = height;

        modelView = gameState.simpleApplication.getRenderManager().createPreView("model3DView", initCamera());
        modelView.setClearFlags(true, true, true);

        offTex = initTexture();

        modelView.setOutputFrameBuffer(initBuffer(offTex, width, height));

        scene = new Node("scene");

        initLight(new Vector3f(-2f, -10f, -5f), .5f);
        initLight(new Vector3f(2f, -10f, 5f), .5f);

        modelNode = new Node("model node");
        scene.attachChild(modelNode);

        scene.updateGeometricState();
        modelView.attachScene(scene);

        viewer = createViewer();
    }

    private FrameBuffer initBuffer(Texture2D offTex, int width, int height) {
        FrameBuffer offBuffer = new FrameBuffer(width, height, 1);

        offBuffer.setDepthBuffer(Image.Format.Depth);
        offBuffer.setColorTexture(offTex);
        return offBuffer;
    }

    private void initLight(Vector3f direction, float intensity) {
        DirectionalLight directional = new DirectionalLight();
        directional.setDirection(direction.normalize());
        directional.setColor(ColorRGBA.White.mult(intensity));
        scene.addLight(directional);
    }

    private Texture2D initTexture() {
        return new Texture2D(width, height, Image.Format.RGBA8);
    }

    private Camera initCamera() {
        Camera camera = new Camera(width, height);
        camera.setFrustumPerspective(15, ((float) width) / height, 1f, 500f);
        camera.setLocation(new Vector3f(8, 6, 10));
        camera.lookAt(new Vector3f(0, 1f, 0), Vector3f.UNIT_Y);
        return camera;
    }

    public ModelViewer withModel(Spatial child) {
        modelView.detachScene(scene);
        modelNode.detachAllChildren();
        if (child != null) {
            modelNode.attachChild(child);
            child.setLocalTranslation(Vector3f.ZERO);
            child.setLocalRotation(Quaternion.IDENTITY);
        }
        scene.updateGeometricState();
        modelView.attachScene(scene);

        return this;
    }

    public ModelViewer withBackgroundColor(ColorRGBA colorRGBA) {
        modelView.detachScene(scene);
        modelView.setBackgroundColor(colorRGBA);
        scene.updateGeometricState();
        modelView.attachScene(scene);

        return this;
    }

    void rotate(float tpv) {
        modelView.detachScene(scene);
        modelNode.rotate(new Quaternion().fromAngleAxis(tpv, Vector3f.UNIT_Y));
        scene.updateGeometricState();
        modelView.attachScene(scene);
    }

    private Picture createViewer() {
        Picture picture = new Picture("viewer");
        picture.setTexture(gameState.assetManager, offTex, true);
        picture.setWidth(width);
        picture.setHeight(height);
        return picture;
    }

    public ModelViewer rotating() {
        viewer.addControl(new RotationControl(this));
        return this;
    }

    public Picture getViewer() {
        return viewer;
    }

    public void updateViewer(Picture picture) {
        picture.setTexture(gameState.assetManager, offTex, true);
    }
}
