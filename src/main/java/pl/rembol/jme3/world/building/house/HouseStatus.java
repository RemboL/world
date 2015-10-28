package pl.rembol.jme3.world.building.house;

import java.util.ArrayList;
import java.util.List;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.ui.Picture;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.building.BuildingStatus;

public class HouseStatus extends BuildingStatus {

    private static final int FIRST_ACTION_SIZE = 40;

    private static final int ACTION_SIZE = 32;

    private House house;

    private Node recruitmentQueueNode;

    private List<Node> unitsInsideNode;

    private Geometry progressRectangle;

    public HouseStatus(House house, GameState gameState) {
        super(house, gameState);
        this.house = house;

        recruitmentQueueNode = new Node("recruitment queue node");
        attachChild(recruitmentQueueNode);
        recruitmentQueueNode.setLocalTranslation(45, 18, 2);

        unitsInsideNode = new ArrayList<>();

        Node unitInsideNode1 = new Node("unit inside node 1");
        attachChild(unitInsideNode1);
        unitsInsideNode.add(unitInsideNode1);
        unitInsideNode1.setLocalTranslation(260, 60, 2);

        Node unitInsideNode2 = new Node("unit inside node 2");
        attachChild(unitInsideNode2);
        unitInsideNode2.setLocalTranslation(292, 60, 2);
        unitsInsideNode.add(unitInsideNode2);

        Node unitInsideNode3 = new Node("unit inside node 3");
        attachChild(unitInsideNode3);
        unitInsideNode3.setLocalTranslation(260, 28, 2);
        unitsInsideNode.add(unitInsideNode3);

        Node unitInsideNode4 = new Node("unit inside node 4");
        attachChild(unitInsideNode4);
        unitInsideNode4.setLocalTranslation(292, 28, 2);
        unitsInsideNode.add(unitInsideNode4);

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
            List<RecruitQueuedAction> queue = house.control().getQueue();

            int offset = 0;
            for (RecruitQueuedAction queueAction : queue) {
                Picture icon = queueAction.getActionIcon();
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

        for (Node unitInsideNode : unitsInsideNode) {
            unitInsideNode.detachAllChildren();
        }

        for (int i = 0; i < house.unitsInside().size(); ++i) {
            unitsInsideNode.get(i).attachChild(house.unitsInside().get(i).getIcon());
            house.unitsInside().get(i).getIcon().setLocalTranslation(0, 0, 0);
        }

    }

}
