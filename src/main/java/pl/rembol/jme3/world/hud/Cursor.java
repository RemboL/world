package pl.rembol.jme3.world.hud;

import pl.rembol.jme3.world.GameState;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;

public class Cursor extends Node {

	public Cursor(Node guiNode) {
		Geometry horizontalLine = new Geometry("cursor", new Line(
				Vector3f.UNIT_X.mult(10f), Vector3f.UNIT_X.mult(-10f)));
		horizontalLine.setMaterial(new Material(GameState.get()
				.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md"));
		Geometry verticalLine = new Geometry("cursor", new Line(
				Vector3f.UNIT_Y.mult(10f), Vector3f.UNIT_Y.mult(-10f)));
		verticalLine.setMaterial(new Material(
				GameState.get().getAssetManager(),
				"Common/MatDefs/Misc/Unshaded.j3md"));

		attachChild(horizontalLine);
		attachChild(verticalLine);

		guiNode.attachChild(this);

		setLocalTranslation(GameState.get().getSettings().getWidth() / 2,
				GameState.get().getSettings().getHeight() / 2, 0);
	}

}
