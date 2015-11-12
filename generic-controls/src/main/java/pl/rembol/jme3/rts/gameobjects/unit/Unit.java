package pl.rembol.jme3.rts.gameobjects.unit;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import pl.rembol.jme3.rts.gameobjects.control.MovingControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithActionQueueControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithDefaultActionControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithMovingControl;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.rts.gameobjects.selection.SelectionIcon;
import pl.rembol.jme3.rts.gameobjects.selection.SelectionNode;
import pl.rembol.jme3.rts.GameState;

abstract public class Unit implements Selectable,
        WithMovingControl, WithActionQueueControl, WithDefaultActionControl  {

    protected GameState gameState;

    protected Node node;

    protected SelectionIcon icon;

    protected Node selectionNode;

    protected BetterCharacterControl control;

    public Unit(GameState gameState, Vector3f position) {
        this.gameState = gameState;
        initNode(gameState.rootNode);
        icon = createIcon(gameState);
        node.setLocalTranslation(position);
        node.setLocalRotation(new Quaternion().fromAngleAxis(
                new Random().nextFloat() * FastMath.PI, Vector3f.UNIT_Y));

        control = new BetterCharacterControl(.6f * getWidth(), 10f, getWidth() * getWidth());

        addControls();
        
        node.addControl(control);

        gameState.bulletAppState.getPhysicsSpace().add(control);

        control.setViewDirection(new Vector3f(new Random().nextFloat() - .5f,
                0f, new Random().nextFloat() - .5f).normalize());

        gameState.unitRegistry.register(this);
    }
    
    protected void addControls() {
        node.addControl(new UnitControl(gameState, this));
        node.addControl(new MovingControl(this));
    }
    
    private void initNode(Node rootNode) {
        node = initNodeWithScale();

        rootNode.attachChild(node);
        node.setShadowMode(RenderQueue.ShadowMode.Cast);
        initAnimation();
    }

    private void initAnimation() {
        AnimControl animationControl = node.getControl(AnimControl.class);
        AnimChannel animationChannel = animationControl.createChannel();
        animationChannel.setAnim("stand");
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
    public SelectionIcon getIcon() {
        return icon;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public Node initNodeWithScale() {
        return (Node) gameState.assetManager.loadModel(getModelName());
    }

    abstract protected String getModelName();
    
    abstract protected SelectionIcon createIcon(GameState gameState);
    
    @Override
    public List<String> getAvailableOrders() {
        return Arrays.asList("move");
    }
}
