package pl.rembol.jme3.rts.input.state;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.order.Order;
import pl.rembol.jme3.rts.gameobjects.order.WithSilhouette;

public class BuildingSilhouetteManager extends AbstractControl {

    private Node silhouette;

    private Material greenMaterial;

    private Material redMaterial;

    private RtsGameState gameState;

    private WithSilhouette orderWithSilhouette;

    public BuildingSilhouetteManager(RtsGameState gameState) {
        this.gameState = gameState;

        greenMaterial = new Material(gameState.assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        greenMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        greenMaterial.setColor("Color", new ColorRGBA(.5f, 1f, .5f, .2f));

        redMaterial = new Material(gameState.assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        redMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        redMaterial.setColor("Color", new ColorRGBA(1f, .5f, .5f, .2f));
    }

    public void createSilhouette(Order<?> currentOrder) {
        if (WithSilhouette.class.isInstance(currentOrder)) {
            Vector3f newPosition = getNewPosition();

            if (newPosition != null) {
                orderWithSilhouette = WithSilhouette.class.cast(currentOrder);
                silhouette = orderWithSilhouette.createNode();
                silhouette.setMaterial(greenMaterial);
                silhouette.setLocalTranslation(newPosition);
                silhouette.addControl(this);
                silhouette.setQueueBucket(Bucket.Transparent);
                gameState.rootNode.attachChild(silhouette);
            }
        }
    }

    public Vector3f getSilhouettePosition() {
        if (silhouette != null) {
            return silhouette.getLocalTranslation();
        }

        return null;
    }

    public void removeSilhouette() {
        if (silhouette != null) {
            if (silhouette.getControl(BuildingSilhouetteManager.class) != null) {
                silhouette.removeControl(this);
            }

            gameState.rootNode.detachChild(silhouette);
            silhouette = null;
        }
        orderWithSilhouette = null;
    }

    @Override
    protected void controlRender(RenderManager arg0, ViewPort arg1) {
    }

    @Override
    protected void controlUpdate(float tpf) {
        Vector3f newPosition = getNewPosition();
        if (newPosition != null) {
            silhouette.setLocalTranslation(newPosition);
            if (orderWithSilhouette.requiredFreeWidth() < 0
                    || gameState.unitRegistry.isSpaceFreeWithBuffer(newPosition, orderWithSilhouette.requiredFreeWidth())) {
                silhouette.setMaterial(greenMaterial);
            } else {
                silhouette.setMaterial(redMaterial);
            }
        }

    }

    private Vector3f getNewPosition() {
        Vector3f clickPosition = getCollisionWithTerrain();
        if (clickPosition != null) {
            Vector2f roundedPosition2d = new Vector2f(
                    Math.round(clickPosition.x), Math.round(clickPosition.z));
            return gameState.terrain.getGroundPosition(roundedPosition2d);
        }

        return null;
    }

    private Ray getClickRay() {
        Vector2f click2d = gameState.inputManager.getCursorPosition();

        Vector3f click3d = gameState.camera.getWorldCoordinates(
                new Vector2f(click2d.getX(), click2d.getY()), 0f);

        Vector3f dir = gameState.camera
                .getWorldCoordinates(
                        new Vector2f(click2d.getX(), click2d.getY()), 1f)
                .subtractLocal(click3d).normalize();

        return new Ray(click3d, dir);
    }

    private Vector3f getCollisionWithTerrain() {
        Ray ray = getClickRay();

        CollisionResults results = new CollisionResults();
        gameState.terrain.getTerrain().collideWith(ray, results);

        CollisionResult collision = results.getClosestCollision();

        if (collision != null) {
            return collision.getContactPoint();
        }

        return null;
    }

}
