package pl.rembol.jme3.world.hud;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;

public class Cursor extends Node {

	public Cursor(Node guiNode, AppSettings settings, AssetManager assetManager) {
		Geometry horizontalLine = new Geometry("cursor", new Line(
				Vector3f.UNIT_X.mult(10f), Vector3f.UNIT_X.mult(-10f)));
		horizontalLine.setMaterial(new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md"));
		Geometry verticalLine = new Geometry("cursor", new Line(
				Vector3f.UNIT_Y.mult(10f), Vector3f.UNIT_Y.mult(-10f)));
		verticalLine.setMaterial(new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md"));

		attachChild(horizontalLine);
		attachChild(verticalLine);

		guiNode.attachChild(this);

		setLocalTranslation(settings.getWidth() / 2, settings.getHeight() / 2,
				0);
	}
}
