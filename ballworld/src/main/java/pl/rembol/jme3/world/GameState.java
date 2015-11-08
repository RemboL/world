package pl.rembol.jme3.world;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.system.AppSettings;
import pl.rembol.jme3.world.ballman.order.OrderFactory;
import pl.rembol.jme3.world.hud.ActionBox;
import pl.rembol.jme3.world.input.CommandKeysListener;
import pl.rembol.jme3.world.input.DragSelectionManager;
import pl.rembol.jme3.world.input.MouseClickListener;
import pl.rembol.jme3.world.input.state.BuildingSilhouetteManager;
import pl.rembol.jme3.world.input.state.InputStateManager;
import pl.rembol.jme3.world.input.state.SelectionManager;
import pl.rembol.jme3.world.player.PlayerService;
import pl.rembol.jme3.world.resources.ResourceTypes;

public class GameState extends pl.rembol.jme3.rts.GameState {
    public final UnitRegistry unitRegistry;

    public final SelectionManager selectionManager;

    public final PlayerService playerService;

    public final BuildingSilhouetteManager buildingSilhouetteManager;

    public final OrderFactory orderFactory;

    public final ActionBox actionBox;

    public final InputStateManager inputStateManager;

    public final DragSelectionManager dragSelectionManager;

    public final MouseClickListener mouseClickListener;

    public final BallManUnitRegistry ballManUnitRegistry;

    public GameState(SimpleApplication simpleApplication, AppSettings settings, BulletAppState bulletAppState) {
        super(simpleApplication, settings, bulletAppState);


        actionBox = new ActionBox(this);

        unitRegistry = new UnitRegistry(this);
        selectionManager = new SelectionManager(this);
        playerService = new PlayerService(this);
        buildingSilhouetteManager = new BuildingSilhouetteManager(this);
        orderFactory = new OrderFactory(this);
        inputStateManager = new InputStateManager(this);
        new CommandKeysListener(this);
        dragSelectionManager = new DragSelectionManager(this);
        mouseClickListener = new MouseClickListener(this);

        ballManUnitRegistry = new BallManUnitRegistry(this);


        initResources(ResourceTypes.values());
        playerService.setResourceTypeList(ResourceTypes.values());
    }

}
