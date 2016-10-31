package pl.rembol.jme3.world.rabbit;

import java.util.Random;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.control.CharacterBasedControl;
import pl.rembol.jme3.rts.gameobjects.control.MovingControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithMovingControl;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.rts.gameobjects.selection.SelectionIcon;
import pl.rembol.jme3.rts.gameobjects.selection.SelectionNode;

public class Rabbit extends AbstractControl implements Selectable, WithMovingControl {

    private RtsGameState gameState;

    private Node node;

    private SelectionIcon icon;

    private CharacterBasedControl control;

    private Node selectionNode;

    private RabbitStatus status;

    public Rabbit(RtsGameState gameState, Vector2f position) {
        this(gameState, gameState.terrain.getGroundPosition(position));
    }

    public Rabbit(RtsGameState gameState, Vector3f position) {
        this.gameState = gameState;

        initNode(gameState.rootNode);
        icon = new SelectionIcon(gameState, this, "rabbit");
        node.setLocalTranslation(position);
        node.setLocalRotation(new Quaternion().fromAngleAxis(
                new Random().nextFloat() * FastMath.PI, Vector3f.UNIT_Y));

        control = new CharacterBasedControl(gameState, .6f, 10f, 1);
        node.addControl(control);

        node.addControl(new RabbitControl(gameState, this));
        node.addControl(new MovingControl(this));

        gameState.bulletAppState.getPhysicsSpace().add(control);

        control.setViewDirection(new Vector3f(new Random().nextFloat() - .5f,
                0f, new Random().nextFloat() - .5f).normalize());

        gameState.unitRegistry.register(this);
    }

    @Override
    public Node initNodeWithScale() {
        return (Node) gameState.assetManager.loadModel("rabbit/rabbit.mesh.xml");
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
            selectionNode = new SelectionNode(gameState);
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
            status = new RabbitStatus(gameState);
        }

        status.update();
        return status;
    }

    @Override
    public SelectionIcon getIcon() {
        return icon;
    }

}
