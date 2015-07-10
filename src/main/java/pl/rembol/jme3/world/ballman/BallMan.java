package pl.rembol.jme3.world.ballman;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.controls.MovingControl;
import pl.rembol.jme3.world.input.state.SelectionManager;
import pl.rembol.jme3.world.input.state.StatusDetails;
import pl.rembol.jme3.world.interfaces.WithMovingControl;
import pl.rembol.jme3.world.particles.SparkParticleEmitter;
import pl.rembol.jme3.world.player.Player;
import pl.rembol.jme3.world.player.PlayerService;
import pl.rembol.jme3.world.player.WithOwner;
import pl.rembol.jme3.world.save.BallManDTO;
import pl.rembol.jme3.world.save.UnitDTO;
import pl.rembol.jme3.world.selection.Destructable;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.selection.SelectionIcon;
import pl.rembol.jme3.world.selection.SelectionNode;
import pl.rembol.jme3.world.smallobject.SmallObject;
import pl.rembol.jme3.world.smallobject.tools.Tool;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class BallMan implements Selectable, WithOwner, Destructable,
        WithMovingControl, ApplicationContextAware {

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

    private Node node;
    private SelectionIcon icon;
    private Node selectionNode;
    private BetterCharacterControl control;
    private static final int MAX_HP = 50;
    private int hp = MAX_HP;

    private Map<Hand, SmallObject> wielded = new HashMap<>();

    private Player owner;
    private ApplicationContext applicationContext;

    private Inventory inventory = new Inventory();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void init(Vector2f position) {
        init(terrain.getGroundPosition(position));
    }

    public void init(Vector3f position) {
        initNode(rootNode);
        icon = new SelectionIcon(this, "ballman", assetManager);
        node.setLocalTranslation(position);
        node.setLocalRotation(new Quaternion().fromAngleAxis(
                new Random().nextFloat() * FastMath.PI, Vector3f.UNIT_Y));

        control = new BetterCharacterControl(.6f, 10f, 1);

        node.addControl(new BallManControl(applicationContext, this));
        node.addControl(new MovingControl(this));

        bulletAppState.getPhysicsSpace().add(control);

        node.addControl(control);
        control.setViewDirection(new Vector3f(new Random().nextFloat() - .5f,
                0f, new Random().nextFloat() - .5f).normalize());

        unitRegistry.register(this);
    }

    @Override
    public void strike(int strength) {
        hp -= strength;
        new SparkParticleEmitter(applicationContext, ColorRGBA.Red, strength,
                node).emit();

        if (hp <= 0) {
            destroy();
        }

        selectionManager.updateStatusIfSingleSelected(this);
    }

    private void destroy() {
        new SparkParticleEmitter(applicationContext, ColorRGBA.Red, 1000,
                rootNode).doSetLocalTranslation(node.getLocalTranslation())
                .emit();

        unitRegistry.unregister(this);
        rootNode.detachChild(node);
        node.removeControl(BallManControl.class);
        node.removeControl(MovingControl.class);
        bulletAppState.getPhysicsSpace().remove(control);
    }

    @Override
    public boolean isDestroyed() {
        return hp <= 0;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public float getWidth() {
        return 1f;
    }

    @Override
    public Node initNodeWithScale() {
        return (Node) assetManager.loadModel("ballman/ballman.mesh.xml");
    }

    private void initNode(Node rootNode) {
        node = initNodeWithScale();

        rootNode.attachChild(node);
        node.setShadowMode(ShadowMode.Cast);
        initAnimation();
    }

    private void initAnimation() {
        AnimControl animationControl = node.getControl(AnimControl.class);
        AnimChannel animationChannel = animationControl.createChannel();
        animationChannel.setAnim("stand");
    }

    public static enum Hand {
        LEFT("hand.L"), RIGHT("hand.R");

        private String bone;

        Hand(String bone) {
            this.bone = bone;
        }

    };

    public void wield(Optional<? extends SmallObject> item, Hand hand) {
        if (wielded.get(hand) != null) {
            dropAndDestroy(hand);
        }

        if (item.isPresent()) {
            item.get().attach(
                    node.getControl(SkeletonControl.class).getAttachmentsNode(
                            hand.bone));

            wielded.put(hand, item.get());
        }
    }

    public SmallObject getWieldedObject(Hand hand) {
        return wielded.get(hand);
    }

    public void drop(Hand hand) {
        if (wielded.get(hand) != null) {
            wielded.get(hand).detach();
            wielded.remove(hand);
        }
    }

    public void dropAndDestroy(Hand hand) {
        if (wielded.get(hand) != null) {
            wielded.get(hand).detach(0);
            wielded.remove(hand);
        }
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
    public StatusDetails getStatusDetails() {
        if (playerService.getActivePlayer().equals(owner)) {
            return new StatusDetails(getStatusText(), inventory.tools());
        } else {
            return new StatusDetails(getStatusText());
        }
    }

    private List<String> getStatusText() {
        return Arrays.asList("BallMan", "hp: " + hp + " / " + MAX_HP, "owner: "
                + owner.getName());
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player player) {
        this.owner = player;

        owner.updateHousing();

        updateColor();
    }

    @Override
    public String[] getGeometriesWithChangeableColor() {
        return new String[] { "skin" };
    }

    @Override
    public UnitDTO save(String key) {
        return new BallManDTO(key, this);
    }

    @Override
    public void load(UnitDTO unit) {
        if (BallManDTO.class.isInstance(unit)) {
            init(new Vector2f(unit.getPosition().x, unit.getPosition().z));
            this.setOwner(playerService.getPlayer(BallManDTO.class.cast(unit)
                    .getPlayer()));
        }
    }

    public Inventory inventory() {
        return inventory;
    }

    public void addToInventory(Tool tool) {
        inventory.add(tool);
    }

    public BallManControl control() {
        return node.getControl(BallManControl.class);
    }

    @Override
    public SelectionIcon getIcon() {
        return icon;
    }

}
