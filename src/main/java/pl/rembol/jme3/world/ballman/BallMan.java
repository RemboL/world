package pl.rembol.jme3.world.ballman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import pl.rembol.jme3.world.Tree;
import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.ballman.action.Action;
import pl.rembol.jme3.world.ballman.action.AttackAction;
import pl.rembol.jme3.world.ballman.action.GatherResourcesAction;
import pl.rembol.jme3.world.ballman.action.MoveTowardsLocationAction;
import pl.rembol.jme3.world.ballman.action.MoveTowardsTargetAction;
import pl.rembol.jme3.world.input.state.SelectionManager;
import pl.rembol.jme3.world.interfaces.WithDefaultAction;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.particles.SparkParticleEmitter;
import pl.rembol.jme3.world.player.Player;
import pl.rembol.jme3.world.player.PlayerService;
import pl.rembol.jme3.world.player.WithOwner;
import pl.rembol.jme3.world.save.BallManDTO;
import pl.rembol.jme3.world.save.UnitDTO;
import pl.rembol.jme3.world.selection.Destructable;
import pl.rembol.jme3.world.selection.Selectable;
import pl.rembol.jme3.world.selection.SelectionNode;
import pl.rembol.jme3.world.smallobject.SmallObject;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

public class BallMan extends AbstractControl implements Selectable,
		WithDefaultAction, WithOwner, Destructable, ApplicationContextAware {

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
	private Node selectionNode;
	private BetterCharacterControl control;
	private static final int MAX_HP = 50;
	private int hp = MAX_HP;

	private Vector3f targetDirection;
	private float targetVelocity = 0;
	private List<Action> actionQueue = new ArrayList<>();

	private SmallObject wielded = null;
	private AnimControl animationControl;
	private AnimChannel animationChannel;
	private Player owner;
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void init(Vector2f position) {
		init(terrain.getGroundPosition(position));
	}

	public void init(Vector3f position) {
		initNode(rootNode);
		node.setLocalTranslation(position);
		node.setLocalRotation(new Quaternion().fromAngleAxis(
				new Random().nextFloat() * FastMath.PI, Vector3f.UNIT_Y));

		control = new BetterCharacterControl(.6f, 10f, 1);

		node.addControl(this);

		bulletAppState.getPhysicsSpace().add(control);

		node.addControl(control);
		control.setViewDirection(new Vector3f(new Random().nextFloat() - .5f,
				0f, new Random().nextFloat() - .5f).normalize());
		control.setWalkDirection(control.getViewDirection().mult(
				new Random().nextFloat() * 5f));

		unitRegistry.register(this);
	}

	@Override
	public void strike(int strength) {
		hp -= strength;
		new SparkParticleEmitter(applicationContext, ColorRGBA.Red, strength,
				node).emit();

		boolean updateSelection = false;
		if (selectionNode != null) {
			updateSelection = true;
		}

		if (hp <= 0) {
			destroy();
		}

		if (updateSelection) {
			selectionManager.updateSelectionText();
		}
	}

	private void destroy() {
		new SparkParticleEmitter(applicationContext, ColorRGBA.Red, 1000,
				rootNode).doSetLocalTranslation(node.getLocalTranslation())
				.emit();

		unitRegistry.unregister(this);
		rootNode.detachChild(node);
		node.removeControl(this);
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

	public float getWalkSpeed() {
		return control.getWalkDirection().length();
	}

	@Override
	public Node initNodeWithScale() {
		return (Node) assetManager.loadModel("ballman/ballman.mesh.xml");
	}

	private void initNode(Node rootNode) {
		node = initNodeWithScale();

		rootNode.attachChild(node);
		node.setShadowMode(ShadowMode.Cast);
		animationControl = node.getControl(AnimControl.class);
		animationChannel = animationControl.createChannel();
		animationChannel.setAnim("stand");
	}

	public void wield(SmallObject item) {
		if (wielded != null) {
			dropAndDestroy();
		}

		item.attach(node.getControl(SkeletonControl.class).getAttachmentsNode(
				"hand.R"));

		wielded = item;

	}

	public SmallObject getWieldedObject() {
		return wielded;
	}

	public void drop() {
		if (wielded != null) {
			wielded.detach();
			wielded = null;
		}
	}

	public void dropAndDestroy() {
		if (wielded != null) {
			wielded.detach(0);
			wielded = null;
		}
	}

	public void lookTowards(WithNode target) {
		lookTowards(target.getNode().getWorldTranslation());
	}

	public void lookTowards(Vector3f location) {
		targetDirection = location.subtract(node.getWorldTranslation()).setY(0)
				.normalize();
	}

	public void setTargetVelocity(float targetVelocity) {
		this.targetVelocity = targetVelocity;
	}

	public Vector3f getLocation() {
		return node.getWorldTranslation();
	}

	public void setAction(Action action) {
		if (!actionQueue.isEmpty()) {
			actionQueue.get(0).stop();
		}

		actionQueue.clear();
		actionQueue.add(action);
	}

	public void addAction(Action action) {
		actionQueue.add(action);
	}

	public void addActionOnStart(Action action) {
		if (!actionQueue.isEmpty()) {
			actionQueue.get(0).stop();
		}

		actionQueue.add(0, action);
	}

	public void setAnimation(String animationName, LoopMode loopMode) {
		animationChannel.setAnim(animationName);
		animationChannel.setLoopMode(loopMode);
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
	public void performDefaultAction(WithNode target) {
		if (target instanceof Tree) {
			setAction(applicationContext.getAutowireCapableBeanFactory()
					.createBean(GatherResourcesAction.class)
					.init(this, Tree.class.cast(target)));
		} else if (WithOwner.class.isInstance(target)
				&& !WithOwner.class.cast(target).getOwner().equals(owner)
				&& Destructable.class.isInstance(target)) {
			setAction(applicationContext.getAutowireCapableBeanFactory()
					.createBean(AttackAction.class)
					.init(Destructable.class.cast(target)));
		} else {
			setAction(applicationContext.getAutowireCapableBeanFactory()
					.createBean(MoveTowardsTargetAction.class).init(target, 5f));
		}
	}

	@Override
	public void performDefaultAction(Vector2f target) {
		setAction(applicationContext.getAutowireCapableBeanFactory()
				.createBean(MoveTowardsLocationAction.class).init(target, 1f));
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (!actionQueue.isEmpty()) {

			Action action = actionQueue.get(0);

			action.act(this, tpf);

			if (action.isFinished(this)) {
				action.finish();
				actionQueue.remove(action);
				animationChannel.setAnim("stand");
			}
		}

		if (targetDirection != null) {
			control.setViewDirection(control.getViewDirection()
					.add(targetDirection).setY(0).normalize());
		}

		control.setWalkDirection(control.getViewDirection().mult(
				(targetVelocity + getWalkSpeed()) / 2));
	}

	@Override
	protected void controlRender(RenderManager paramRenderManager,
			ViewPort paramViewPort) {
	}

	@Override
	public List<String> getStatusText() {
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
	public String getIconName() {
		return "ballman";
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

}
