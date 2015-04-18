package pl.rembol.jme3.world.ballman.order;

import org.springframework.beans.factory.annotation.Autowired;

import pl.rembol.jme3.world.input.state.SelectionManager;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.Vector2f;

public class SelectOrder extends Order<Selectable> {

	@Autowired
	private SelectionManager selectionManager;

	@Override
	public void perform(Selectable target) {
		selectionManager.select(target);
	}

	@Override
	protected void doPerform(Selectable ballMan, Vector2f location) {
		// doNothing
	}

	@Override
	protected void doPerform(Selectable ballMan, Selectable target) {
		// doNothing
	}

}
