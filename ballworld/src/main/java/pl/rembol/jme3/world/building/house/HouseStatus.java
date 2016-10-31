package pl.rembol.jme3.world.building.house;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.world.building.BuildingStatus;

import java.util.ArrayList;
import java.util.List;

public class HouseStatus extends BuildingStatus {

    private static final int FIRST_ACTION_SIZE = 40;

    private static final int ACTION_SIZE = 32;

    private static final int INSIDE_GRID_WIDTH = 2;

    private static final int INSIDE_GRID_HEIGHT = 2;

    private House house;

    private Node recruitmentQueueNode;

    private List<Node> unitsInsideNode;

    private Geometry progressRectangle;

    public HouseStatus(House house, RtsGameState gameState) {
        super(house, gameState);
        this.house = house;

        recruitmentQueueNode = new Node("recruitment queue node");
        attachChild(recruitmentQueueNode);
        recruitmentQueueNode.setLocalTranslation(45, 18, 2);

        unitsInsideNode = new ArrayList<>();

        for (int i = 0; i < INSIDE_GRID_HEIGHT; ++i) {
            for (int j = 0; j < INSIDE_GRID_WIDTH; ++j) {
                Node unitInsideNode = new Node("unit inside node");
                attachChild(unitInsideNode);
                unitsInsideNode.add(unitInsideNode);
                unitInsideNode.setLocalTranslation(260 + j * ACTION_SIZE, 60 - i * ACTION_SIZE, 2);
            }
        }

        progressRectangle = new Geometry("progress rectangle", new Quad(1f, 1f));
        Material greenMaterial = new Material(gameState.assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        greenMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        greenMaterial.setColor("Color", new ColorRGBA(.5f, 1f, .5f, .2f));
        progressRectangle.setMaterial(greenMaterial);
        progressRectangle.setLocalTranslation(0, 0, 1);
    }

    @Override
    public void update() {
        super.update();
        if (recruitmentQueueNode == null) {
            return;
        }
        recruitmentQueueNode.detachAllChildren();

        if (house.control() != null && house.control().isRecruiting()) {
            updateRecruitmentStatus();
        }

        updateInsideStatus();

    }

    private void updateInsideStatus() {
        unitsInsideNode.forEach(Node::detachAllChildren);

        for (int i = 0; i < house.unitsInside().size(); ++i) {
            unitsInsideNode.get(i).attachChild(house.unitsInside().get(i).getIcon());
            house.unitsInside().get(i).getIcon().setLocalTranslation(0, 0, 0);
        }
    }

    private void updateRecruitmentStatus() {
        List<RecruitQueuedAction> queue = house.control().getQueue();

        int offset = 0;
        for (RecruitQueuedAction queueAction : queue) {
            Spatial icon = queueAction.getActionIcon();
            recruitmentQueueNode.attachChild(icon);
            icon.setLocalTranslation(offset, 0, 0);
            if (queueAction == queue.get(0)) {
                recruitmentQueueNode.attachChild(progressRectangle);
                progressRectangle.setLocalScale(
                        new Vector3f(FIRST_ACTION_SIZE * queueAction.progress(), FIRST_ACTION_SIZE, 1));
                icon.setLocalScale(new Vector3f(FIRST_ACTION_SIZE, FIRST_ACTION_SIZE, 1));
                offset += 48;
            } else {
                offset += ACTION_SIZE;
            }
        }
    }

}
