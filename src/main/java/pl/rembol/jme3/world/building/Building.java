package pl.rembol.jme3.world.building;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import pl.rembol.jme3.world.Solid;
import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.input.state.SelectionManager;
import pl.rembol.jme3.world.input.state.StatusDetails;
import pl.rembol.jme3.world.player.Player;
import pl.rembol.jme3.world.player.PlayerService;
import pl.rembol.jme3.world.player.WithOwner;
import pl.rembol.jme3.world.selection.Destructable;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.selection.SelectionIcon;
import pl.rembol.jme3.world.selection.SelectionNode;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;

public abstract class Building implements Selectable, WithOwner, Destructable,
        Solid, ApplicationContextAware {

    private RigidBodyControl control;
    private Node node;
    private Node building;
    private SelectionIcon icon;
    private SelectionNode selectionNode;
    protected Player owner;
    private int hp;
    protected ApplicationContext applicationContext;

    @Autowired
    private Terrain terrain;

    @Autowired
    private Node rootNode;

    @Autowired
    private BulletAppState bulletAppState;

    @Autowired
    protected AssetManager assetManager;

    @Autowired
    private SelectionManager selectionManager;

    @Autowired
    private UnitRegistry unitRegistry;

    @Autowired
    protected PlayerService playerService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Building init(Vector2f position) {
        return init(position, false);
    }

    public Building init(Vector2f position, boolean startUnderGround) {
        return init(terrain.getGroundPosition(position), startUnderGround);
    }

    public Building init(Vector3f position, boolean startUnderGround) {

        node = new Node();
        building = initNodeWithScale();
        icon = new SelectionIcon(this, getIconName(), assetManager);

        building.setShadowMode(ShadowMode.Cast);
        if (startUnderGround) {
            building.setLocalTranslation(Vector3f.UNIT_Y.mult(-getHeight()));
        } else {
            finishBuilding();
        }

        node.setLocalTranslation(position);

        node.attachChild(building);
        rootNode.attachChild(node);

        control = new RigidBodyControl(new BoxCollisionShape(new Vector3f(
                getWidth(), getHeight(), getWidth())), 0f);
        building.addControl(control);

        bulletAppState.getPhysicsSpace().add(control);

        unitRegistry.register(this);

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
            selectionNode = new SelectionNode(assetManager);
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
    public StatusDetails getStatusDetails() {
        if (isConstructed()) {
            return new StatusDetails(statusLines());

        } else {
            return new StatusDetails(Arrays.asList(getName(), //
                    "owner: " + owner.getName(), //
                    "Under construction"));
        }
    }

    protected List<String> statusLines() {
        return Arrays.asList(getName(), //
                "hp: " + hp + " / " + getMaxHp(), //
                "owner: " + owner.getName());
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
            owner.updateHousingLimit();
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

        selectionManager.updateStatusIfSingleSelected(this);
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

    private void destroy() {
        unitRegistry.unregister(this);
        bulletAppState.getPhysicsSpace().remove(control);

        for (int i = getNode().getNumControls() - 1; i >= 0; --i) {
            getNode().removeControl(getNode().getControl(i));
        }

        applicationContext.getAutowireCapableBeanFactory()
                .createBean(BuildingDestructionControl.class).init(this);
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
        return (Node) assetManager.loadModel(getNodeFileName());
    }

    protected abstract String getNodeFileName();

    public abstract float getHeight();

    public abstract float getWidth();

    public abstract String getName();

    public abstract String getIconName();

    public abstract int getMaxHp();

}
