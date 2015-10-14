package pl.rembol.jme3.world.ballman;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.ballman.hunger.HungerControl;
import pl.rembol.jme3.world.controls.MovingControl;
import pl.rembol.jme3.world.input.state.SelectionManager;
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

public class BallMan implements Selectable, WithOwner, Destructable,
        WithMovingControl {

    @Autowired
    private UnitRegistry unitRegistry;

    @Autowired
    private SelectionManager selectionManager;

    @Autowired
    private GameState gameState;

    @Autowired
    private PlayerService playerService;

    private Node node;

    private SelectionIcon icon;

    private Node selectionNode;

    private BetterCharacterControl control;

    protected static final int MAX_HP = 50;

    private int hp = MAX_HP;

//    private BallManStatus status;

    private Map<Hand, SmallObject> wielded = new HashMap<>();

    private Player owner;

    private Inventory inventory = new Inventory();

    public void init(Vector2f position) {
        init(gameState.terrain.getGroundPosition(position));
    }

    public void init(Vector3f position) {
        initNode(gameState.rootNode);
        icon = new SelectionIcon(gameState, this, "ballman");
        node.setLocalTranslation(position);
        node.setLocalRotation(new Quaternion().fromAngleAxis(
                new Random().nextFloat() * FastMath.PI, Vector3f.UNIT_Y));

        control = new BetterCharacterControl(.6f, 10f, 1);

        node.addControl(new BallManControl(gameState, this));
        node.addControl(new MovingControl(this));
        node.addControl(new HungerControl(gameState, this));

        gameState.bulletAppState.getPhysicsSpace().add(control);

        node.addControl(control);
        control.setViewDirection(new Vector3f(new Random().nextFloat() - .5f,
                0f, new Random().nextFloat() - .5f).normalize());

        unitRegistry.register(this);
    }

    @Override
    public void strike(int strength) {
        hp -= strength;
        new SparkParticleEmitter(gameState, ColorRGBA.Red, strength, node).emit();

        if (hp <= 0) {
            destroy();
        }

        selectionManager.updateStatusIfSingleSelected(this);
    }

    private void destroy() {
        new SparkParticleEmitter(gameState, ColorRGBA.Red, 1000,
                gameState.rootNode).doSetLocalTranslation(node.getLocalTranslation())
                .emit();

        unitRegistry.unregister(this);
        gameState.rootNode.detachChild(node);
        node.removeControl(BallManControl.class);
        node.removeControl(MovingControl.class);
        node.removeControl(HungerControl.class);
        gameState.bulletAppState.getPhysicsSpace().remove(control);
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

    public int getHp() {
        return hp;
    }

    @Override
    public Node initNodeWithScale() {
        return (Node) gameState.assetManager.loadModel("ballman/ballman.mesh.xml");
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

    }

    ;

    public void wield(Optional<? extends SmallObject> item, Hand hand) {
        if (wielded.get(hand) != null) {
            dropAndDestroy(hand);
        }

        if (item.isPresent()) {
            item.get().attach(
                    node.getControl(SkeletonControl.class).getAttachmentsNode(
                            hand.bone));

            item.get().getNode().setLocalRotation(Quaternion.IDENTITY);
            wielded.put(hand, item.get());

            selectionManager.updateStatusIfSingleSelected(this);
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
            wielded.get(hand).detach();
            wielded.remove(hand);
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
    public Node getStatusDetails() {
//        if (status == null) {
//            status = new BallManStatus(this, applicationContext);
//        }
//
//        status.update();
//        return status;
        return null;
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
        return new String[]{ "skin" };
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
        selectionManager.updateStatusIfSingleSelected(this);
    }

    public BallManControl control() {
        return node.getControl(BallManControl.class);
    }

    @Override
    public SelectionIcon getIcon() {
        return icon;
    }

    public HungerControl hunger() {
        return node.getControl(HungerControl.class);
    }

}
