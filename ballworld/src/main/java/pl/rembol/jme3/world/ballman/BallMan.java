package pl.rembol.jme3.world.ballman;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import pl.rembol.jme3.rts.gameobjects.control.ActionQueueControl;
import pl.rembol.jme3.rts.gameobjects.control.DefaultActionControl;
import pl.rembol.jme3.rts.gameobjects.control.MovingControl;
import pl.rembol.jme3.rts.gameobjects.selection.Destructable;
import pl.rembol.jme3.rts.gameobjects.selection.SelectionIcon;
import pl.rembol.jme3.rts.gameobjects.unit.Unit;
import pl.rembol.jme3.rts.particles.SparkParticleEmitter;
import pl.rembol.jme3.rts.player.Player;
import pl.rembol.jme3.rts.player.WithOwner;
import pl.rembol.jme3.rts.save.UnitDTO;
import pl.rembol.jme3.rts.smallobjects.SmallObject;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.world.ballman.hunger.HungerControl;
import pl.rembol.jme3.world.ballmanunitregistry.BallManUnitRegistry;
import pl.rembol.jme3.world.building.house.House;
import pl.rembol.jme3.world.resources.ResourceTypes;
import pl.rembol.jme3.world.save.BallManDTO;
import pl.rembol.jme3.world.smallobject.tools.Tool;

public class BallMan extends Unit implements WithOwner, Destructable {

    private Node controlNode;

    protected static final int MAX_HP = 50;

    private int hp = MAX_HP;

    private BallManStatus status;

    private Map<Hand, SmallObject> wielded = new HashMap<>();

    private Player owner;

    private Inventory inventory = new Inventory();

    private GameState gameState;

    private BallManUnitRegistry ballManUnitRegistry;

    public BallMan(GameState gameState, Vector2f position, String player) {
        this(gameState, gameState.terrain.getGroundPosition(position), player);
    }

    public BallMan(GameState gameState, Vector3f position, String player) {
        super(gameState, position);
        this.gameState = gameState;
        ballManUnitRegistry = new BallManUnitRegistry(gameState);

        BetterCharacterControl control = new BetterCharacterControl(.6f, 10f, 1);
        gameState.bulletAppState.getPhysicsSpace().add(control);
        node.addControl(control);

        controlNode = new Node("control node");
        getNode().attachChild(controlNode);

        controlNode.addControl(new BallManControl(gameState, this));
        controlNode.addControl(new MovingControl(this));
        controlNode.addControl(new HungerControl(gameState, this));

        owner = gameState.playerService.getPlayer(player);
    }

    @Override
    public void addControls(pl.rembol.jme3.rts.GameState gameState) {
    }

    @Override
    public void strike(int strength) {
        hp -= strength;
        new SparkParticleEmitter(gameState, ColorRGBA.Red, strength, node).emit();

        if (hp <= 0) {
            destroy();
        }

        gameState.selectionManager.updateStatusIfSingleSelected(this);
    }

    private void destroy() {
        new SparkParticleEmitter(gameState, ColorRGBA.Red, 1000,
                gameState.rootNode).doSetLocalTranslation(node.getLocalTranslation())
                .emit();

        gameState.unitRegistry.unregister(this);
        gameState.rootNode.detachChild(node);
        gameState.bulletAppState.getPhysicsSpace().remove(controlNode.getControl(BetterCharacterControl.class));
        node.detachChild(controlNode);
    }

    @Override
    public boolean isDestroyed() {
        return hp <= 0;
    }

    @Override
    public float getWidth() {
        return 1f;
    }

    public int getHp() {
        return hp;
    }

    public enum Hand {
        LEFT("hand.L"), RIGHT("hand.R");

        private String bone;

        Hand(String bone) {
            this.bone = bone;
        }

    }

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

            gameState.selectionManager.updateStatusIfSingleSelected(this);
        }
    }

    public SmallObject getWieldedObject(Hand hand) {
        return wielded.get(hand);
    }

    public void dropAndDestroy(Hand hand) {
        if (wielded.get(hand) != null) {
            wielded.get(hand).detach();
            wielded.remove(hand);
        }
    }

    @Override
    public Node getStatusDetails() {
        if (status == null) {
            status = new BallManStatus(this, gameState);
        }

        status.update();
        return status;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player player) {
        this.owner = player;

        owner.setResource(ResourceTypes.HOUSING, ballManUnitRegistry.countHousing(owner));

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

    public Inventory inventory() {
        return inventory;
    }

    public void addToInventory(Tool tool) {
        inventory.add(tool);
        gameState.selectionManager.updateStatusIfSingleSelected(this);
    }

    public BallManControl control() {
        return controlNode.getControl(BallManControl.class);
    }

    public HungerControl hunger() {
        return controlNode.getControl(HungerControl.class);
    }

    @Override
    public MovingControl movingControl() {
        return controlNode.getControl(MovingControl.class);
    }

    @Override
    public ActionQueueControl actionQueueControl() {
        return controlNode.getControl(ActionQueueControl.class);
    }

    @Override
    public DefaultActionControl defaultActionControl() {
        return controlNode.getControl(DefaultActionControl.class);
    }

    public Optional<House> isInside() {
        if (node.getParent() == gameState.rootNode) {
            return Optional.empty();
        }

        return gameState.unitRegistry
                .getUserData(this, House.INSIDE_DATA_KEY)
                .filter(House.class::isInstance)
                .map(House.class::cast);
    }

    protected String getModelName() {
        return "ballman/ballman.mesh.xml";
    }

    @Override
    protected SelectionIcon createIcon(pl.rembol.jme3.rts.GameState gameState) {
        return new BallManIcon(gameState, this);
    }

    @Override
    public List<String> getAvailableOrders() {
        return Arrays.asList("move", "flatten", "build", "house", "toolshop", "warehouse", "cancel");
    }
}
