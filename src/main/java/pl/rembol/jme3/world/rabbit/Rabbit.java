package pl.rembol.jme3.world.rabbit;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.controls.MovingControl;
import pl.rembol.jme3.world.input.state.SelectionManager;
import pl.rembol.jme3.world.interfaces.WithMovingControl;
import pl.rembol.jme3.world.player.PlayerService;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.selection.SelectionIcon;
import pl.rembol.jme3.world.selection.SelectionNode;
import pl.rembol.jme3.world.terrain.Terrain;

import java.util.Random;

public class Rabbit extends AbstractControl implements Selectable,
        ApplicationContextAware, WithMovingControl {

    @Autowired
    private Terrain terrain;

    @Autowired
    private Node rootNode;

    @Autowired
    private BulletAppState bulletAppState;

    @Autowired
    private UnitRegistry unitRegistry;

    @Autowired
    private SelectionManager selectionManager;

    @Autowired
    private AssetManager assetManager;

    @Autowired
    private PlayerService playerService;

    private ApplicationContext applicationContext;

    private Node node;
    private SelectionIcon icon;
    private BetterCharacterControl control;
    private Node selectionNode;
    private RabbitStatus status;

    public void init(Vector2f position) {
        init(terrain.getGroundPosition(position));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void init(Vector3f position) {
        initNode(rootNode);
        icon = new SelectionIcon(this, "rabbit", assetManager);
        node.setLocalTranslation(position);
        node.setLocalRotation(new Quaternion().fromAngleAxis(
                new Random().nextFloat() * FastMath.PI, Vector3f.UNIT_Y));

        control = new BetterCharacterControl(.6f, 10f, 1);

        node.addControl(new RabbitControl(applicationContext, this));
        node.addControl(new MovingControl(this));

        bulletAppState.getPhysicsSpace().add(control);

        node.addControl(control);
        control.setViewDirection(new Vector3f(new Random().nextFloat() - .5f,
                0f, new Random().nextFloat() - .5f).normalize());

        unitRegistry.register(this);
    }

    @Override
    public Node initNodeWithScale() {
        return (Node) assetManager.loadModel("rabbit/rabbit.mesh.xml");
    }

    private void initNode(Node rootNode) {
        node = initNodeWithScale();
        node.setLocalTranslation(Vector3f.UNIT_Y.mult(5f));

        rootNode.attachChild(node);
        node.setShadowMode(ShadowMode.Cast);
        AnimControl animationControl = node.getControl(AnimControl.class);
        AnimChannel animationChannel = animationControl.createChannel();
        animationChannel.setAnim("stand");
    }

    @Override
    protected void controlRender(RenderManager arg0, ViewPort arg1) {
    }

    @Override
    protected void controlUpdate(float arg0) {

    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public float getWidth() {
        return 1;
    }

    @Override
    public void select() {
        if (selectionNode == null) {
            selectionNode = new SelectionNode(assetManager);
            node.attachChild(selectionNode);
            selectionNode.setLocalTranslation(0, .3f, 0);
        }
    }

    @Override
    public void deselect() {
        if (selectionNode != null) {
            node.detachChild(selectionNode);
            selectionNode = null;
        }
    }

    @Override
    public Node getStatusDetails() {
        if (status == null) {
            status = new RabbitStatus(applicationContext);
        }

        status.update();
        return status;
    }

    @Override
    public SelectionIcon getIcon() {
        return icon;
    }

}
