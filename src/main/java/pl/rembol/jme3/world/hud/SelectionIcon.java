package pl.rembol.jme3.world.hud;

import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.asset.AssetManager;
import com.jme3.ui.Picture;

public class SelectionIcon extends Picture {

	public static final int SIZE = 32;
	private Selectable selectable;

	public SelectionIcon(Selectable selectable, AssetManager assetManager) {
		super(selectable.getIconName());
		this.selectable = selectable;
		setImage(assetManager, "interface/icons/" + selectable.getIconName()
				+ ".png", true);
		setWidth(SIZE);
		setHeight(SIZE);
	}

	public Selectable getSelectable() {
		return selectable;
	}

}