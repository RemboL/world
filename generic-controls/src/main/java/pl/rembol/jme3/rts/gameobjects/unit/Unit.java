package pl.rembol.jme3.rts.gameobjects.unit;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.control.CharacterBasedControl;
import pl.rembol.jme3.rts.gameobjects.control.MovingControl;
import pl.rembol.jme3.rts.gameobjects.control.MovingPhysicsControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithActionQueueControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithDefaultActionControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithMovingControl;
import pl.rembol.jme3.rts.gameobjects.logger.LoggerControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithLoggerControl;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.rts.gameobjects.selection.SelectionIcon;
import pl.rembol.jme3.rts.gameobjects.selection.SelectionNode;

import java.util.Collections;
import java.util.List;
import java.util.Random;

abstract public class Unit implements Selectable,
        WithMovingControl, WithActionQueueControl, WithDefaultActionControl, WithLoggerControl {

    protected GameState gameState;

    protected Node node;

    protected SelectionIcon icon;

    protected Node selectionNode;

    public Unit(GameState gameState) {
        this.gameState = gameState;
        initNode(null);
    }

    public Unit(GameState gameState, Vector3f position) {
        this(gameState);
        initNode(gameState.rootNode);
        icon = createIcon(gameState);
        node.setLocalTranslation(position);
        node.setLocalRotation(new Quaternion().fromAngleAxis(
                new Random().nextFloat() * FastMath.PI, Vector3f.UNIT_Y));

        addControls(gameState);

        MovingPhysicsControl control = node.getControl(MovingPhysicsControl.class);
        if (control != null) {
            node.getControl(MovingPhysicsControl.class).
                    turnTowards(new Vector3f(new Random().nextFloat() - .5f,
                            0f, new Random().nextFloat() - .5f).normalize());
        }

        gameState.unitRegistry.register(this);
    }

    protected MovingPhysicsControl createCharacterControl(GameState gameState) {
        return new CharacterBasedControl(.6f * getWidth(), 10f, getWidth() * getWidth());
    }

    public void addControls(GameState gameState) {
        node.addControl(new UnitControl(this.gameState, this));
        node.addControl(new MovingControl(this));
        node.addControl(new LoggerControl());

        MovingPhysicsControl control = createCharacterControl(gameState);
        node.addControl(control);
        this.gameState.bulletAppState.getPhysicsSpace().add(control);
    }

    public void removeControls() {
        gameState.bulletAppState.getPhysicsSpace().remove(getNode().getControl(MovingPhysicsControl.class));
        getNode().removeControl(MovingPhysicsControl.class);
        getNode().removeControl(UnitControl.class);
        getNode().removeControl(MovingControl.class);
        getNode().removeControl(LoggerControl.class);
    }

    protected void initNode(Node rootNode) {
        node = initNodeWithScale();

        if (rootNode != null) {
            rootNode.attachChild(node);
        }
        node.setShadowMode(RenderQueue.ShadowMode.Cast);
        initAnimation();
    }

    private void initAnimation() {
        AnimControl animationControl = node.getControl(AnimControl.class);
        if (animationControl != null) {
            AnimChannel animationChannel = animationControl.createChannel();
            animationChannel.setAnim("stand");
        }
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
        return Collections.singletonList("move");
    }
}
