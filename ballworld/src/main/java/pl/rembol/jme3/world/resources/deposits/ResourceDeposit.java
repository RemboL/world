package pl.rembol.jme3.world.resources.deposits;

import java.util.Optional;
import java.util.Random;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.control.CharacterBasedControl;
import pl.rembol.jme3.rts.gameobjects.interfaces.Solid;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.rts.gameobjects.selection.SelectionIcon;
import pl.rembol.jme3.rts.gameobjects.selection.SelectionNode;
import pl.rembol.jme3.rts.resources.units.ResourceUnit;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.smallobject.tools.Tool;

public abstract class ResourceDeposit implements Selectable, Solid {

    private CharacterBasedControl control;

    private Node node;

    private SelectionIcon icon;

    private int hp = 1000;

    private int maxHp = 1000;

    private boolean destroyed = false;

    private SelectionNode selectionNode;

    private ResourceDepositStatus status;

    protected GameState gameState;

    public ResourceDeposit(GameState gameState) {
        this.gameState = gameState;
    }

    public void init(Vector2f position) {
        init(new Vector3f(position.x, gameState.terrain.getTerrain().getHeight(
                new Vector2f(position.x, position.y))
                + gameState.terrain.getTerrain().getLocalTranslation().y, position.y));
    }

    public void init(Vector3f position) {

        node = initNodeWithScale();
        icon = new SelectionIcon(gameState, this, getIconName());

        node.setShadowMode(ShadowMode.Cast);
        gameState.rootNode.attachChild(node);
        node.setLocalTranslation(position);

        control = new CharacterBasedControl(getPhysicsRadius(), 5f, 0);
        node.addControl(control);

        control.setViewDirection(getInitialDirection());

        gameState.bulletAppState.getPhysicsSpace().add(control);

        gameState.unitRegistry.register(this);
    }

    private Vector3f getInitialDirection() {
        switch (getRandomDirectionMode()) {
            case ONLY_4_DIRECTIONS:
                int direction = new Random().nextInt(4);
                switch (direction) {
                    case 1:
                        return Vector3f.UNIT_X;
                    case 2:
                        return Vector3f.UNIT_Z;
                    case 3:
                        return Vector3f.UNIT_Z.mult(-1);
                    case 0:
                    default:
                        return Vector3f.UNIT_X.mult(-1);
                }
            case WHOLE_CIRCLE:
            default:
                return new Vector3f(new Random().nextFloat() - .5f, 0f,
                        new Random().nextFloat() - .5f).normalize();
        }
    }

    public enum RandomDirectionMode {
        ONLY_4_DIRECTIONS, WHOLE_CIRCLE
    }

    protected abstract RandomDirectionMode getRandomDirectionMode();

    protected abstract float getPhysicsRadius();

    @Override
    public Node initNodeWithScale() {
        Node node = (Node) gameState.assetManager
                .loadModel(getModelFileName());
        node.setLocalScale(getPhysicsRadius());
        return node;
    }

    protected abstract String getModelFileName();

    public int getHp() {
        return hp;
    }

    protected void setHp(int hp) {
        this.hp = hp;

        node.setLocalScale(getPhysicsRadius() * (this.hp + this.maxHp)
                / (2 * this.maxHp));

        if (hp < 0) {
            destroy();
        }

        gameState.selectionManager.updateStatusIfSingleSelected(this);
    }

    protected void substractHp(int hp) {
        setHp(this.hp - hp);
    }

    private void destroy() {
        gameState.unitRegistry.unregister(this);
        gameState.bulletAppState.getPhysicsSpace().remove(control);
        node.getParent().detachChild(node);
        this.destroyed = true;
    }

    public boolean isDestroyed() {
        return this.destroyed;
    }

    public Vector3f getLocation() {
        return node.getWorldTranslation();
    }

    public void getChoppedBy(BallMan ballMan) {
        substractHp(1);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void select() {
        if (selectionNode == null) {
            selectionNode = new SelectionNode(gameState);
            node.attachChild(selectionNode);
            selectionNode.setLocalTranslation(0, .1f, 0);
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
            status = new ResourceDepositStatus(this, gameState);
        }

        status.update();
        return status;
    }

    @Override
    public SelectionIcon getIcon() {
        return icon;
    }

    protected abstract String getName();

    public abstract ResourceUnit produceResource();

    public abstract Optional<Class<? extends Tool>> requiredTool();

    public abstract Class<? extends ResourceUnit> givesResource();

    protected abstract String getIconName();
}