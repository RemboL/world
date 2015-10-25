package pl.rembol.jme3.world.ballman;

import com.jme3.scene.Node;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.hud.status.DefaultStatus;
import pl.rembol.jme3.world.smallobject.tools.Tool;

import java.util.ArrayList;
import java.util.List;

public class BallManStatus extends DefaultStatus {

    private List<Node> inventoryIcons = new ArrayList<>();

    private final BallMan ballMan;

    public BallManStatus(BallMan ballMan, GameState gameState) {
        super(gameState);
        this.ballMan = ballMan;

        update();
    }

    public void update() {
        updateText(
                "BallMan",
                "hp: " + ballMan.getHp() + " / " + BallMan.MAX_HP,
                "owner: " + ballMan.getOwner().getName());

        if (gameState.playerService.getActivePlayer().equals(ballMan.getOwner())) {
            clearInventory();
            updateInventory();
        }
    }

    @Override
    protected int getTextLineNumber() {
        return 3;
    }

    private void updateInventory() {
        int offset = 0;
        for (Tool tool : ballMan.inventory().tools()) {
            Node node = new Node("inventory item");
            node.attachChild(tool.icon());
            attachChild(node);
            if (tool.equals(ballMan.getWieldedObject(BallMan.Hand.RIGHT))) {
                node.setLocalScale(1.5f);
                offset += 24;
            } else {
                offset += 16;
            }
            node.move(360 - offset, 14, 1);
        }
    }

    private void clearInventory() {
        inventoryIcons.forEach(this::detachChild);
        inventoryIcons.clear();
    }

}
