package pl.rembol.jme3.world.building;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.Solid;
import pl.rembol.jme3.rts.gameobjects.selection.Destructable;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.rts.gameobjects.selection.SelectionIcon;
import pl.rembol.jme3.rts.gameobjects.selection.SelectionNode;
import pl.rembol.jme3.rts.player.Player;
import pl.rembol.jme3.rts.player.WithOwner;
import pl.rembol.jme3.world.ballmanunitregistry.BallManUnitRegistry;
import pl.rembol.jme3.world.resources.ResourceTypes;

import java.util.ArrayList;
import java.util.List;

public abstract class Building implements Selectable, WithOwner, Destructable, Solid {

    private static final int HOUSING_PER_HOUSE = 10;

    private RigidBodyControl control;

    private Node node;

    private Node building;

    private SelectionIcon icon;

    private SelectionNode selectionNode;

    protected Player owner;

    private int hp;

    private BuildingStatus status;

    protected GameState gameState;

    protected BallManUnitRegistry ballManUnitRegistry;

    public Building(GameState gameState) {
        this.gameState = gameState;
        ballManUnitRegistry = new BallManUnitRegistry(gameState);
    }

    public Building init(Vector2f position) {
        return init(position, false);
    }

    public Building init(Vector2f position, boolean startUnderGround) {
        return init(gameState.terrain.getGroundPosition(position), startUnderGround);
    }

    public Building init(Vector3f position, boolean startUnderGround) {

        node = new Node();
        building = initNodeWithScale();
        icon = new SelectionIcon(gameState, this, initNode());

        building.setShadowMode(ShadowMode.Cast);
        if (startUnderGround) {
            building.setLocalTranslation(Vector3f.UNIT_Y.mult(-getHeight()));
        } else {
            finishBuilding();
        }

        node.setLocalTranslation(position);

        node.attachChild(building);
        gameState.rootNode.attachChild(node);

        control = new RigidBodyControl(new BoxCollisionShape(new Vector3f(
                getWidth(), getHeight(), getWidth())), 0f);
        building.addControl(control);

        gameState.bulletAppState.getPhysicsSpace().add(control);

        gameState.unitRegistry.register(this);

        hp = getMaxHp();

        return this;
    }

    public Vector3f getLocation() {
        return building.getWorldTranslation();
    }

    @Override
    public Node getNode() {
        return building;
    }

    @Override
    public void select() {
        if (selectionNode == null) {
            selectionNode = new SelectionNode(gameState);
            node.attachChild(selectionNode);
            selectionNode.setLocalScale(getWidth());
            selectionNode.setLocalTranslation(0, .2f, 0);
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
            status = new BuildingStatus(this, gameState);
        }

        status.update();
        return status;
    }

    protected String[] statusLines() {
        return new String[]{ getName(),
                "hp: " + hp + " / " + getMaxHp(),
                "owner: " + owner.getName() };
    }

    public boolean isConstructed() {
        return getNode().getControl(ConstructionSite.class) == null;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player player) {
        this.owner = player;
        updateColor();
    }

    public void finishBuilding() {
        if (owner != null) {
            owner.setResourceLimit(ResourceTypes.HOUSING, ballManUnitRegistry.getHousesByOwner(owner).size()
                    * HOUSING_PER_HOUSE);
        }

        for (Control control : createControls()) {
            building.addControl(control);
        }
    }

    protected List<Control> createControls() {
        return new ArrayList<>();
    }

    @Override
    public void strike(int strength) {
        hp -= strength;

        if (hp <= 0) {
            destroy();
        }

        gameState.selectionManager.updateStatusIfSingleSelected(this);
    }

    public int getHp() {
        return hp;
    }

    @Override
    public boolean isDestroyed() {
        return hp <= 0;
    }

    public Node getParentNode() {
        return node;
    }

    protected void destroy() {
        gameState.unitRegistry.unregister(this);
        gameState.bulletAppState.getPhysicsSpace().remove(control);

        for (int i = getNode().getNumControls() - 1; i >= 0; --i) {
            getNode().removeControl(getNode().getControl(i));
        }

        new BuildingDestructionControl(gameState, this);
    }

    public Node initNodeWithScale() {
        Node node = initNode();
        node.setLocalScale(getWidth());
        return node;
    }

    @Override
    public SelectionIcon getIcon() {
        return icon;
    }

    public Node initNode() {
        return (Node) gameState.assetManager.loadModel(getNodeFileName());
    }

    protected abstract String getNodeFileName();

    public abstract float getHeight();

    public abstract float getWidth();

    public abstract String getName();

    public abstract int getMaxHp();

}
