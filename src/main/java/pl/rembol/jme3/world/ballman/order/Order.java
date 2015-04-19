package pl.rembol.jme3.world.ballman.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public abstract class Order<OrderedType extends Selectable> implements
		ApplicationContextAware {

	protected List<OrderedType> selected = new ArrayList<>();

	protected ApplicationContext applicationContext;

	public void perform(Vector3f location) {
		perform(new Vector2f(location.x, location.z));
	}

	public void perform(Vector2f location) {
		for (OrderedType ballMan : selected) {
			doPerform(ballMan, location);
		}
	}

	public void perform(WithNode target) {
		for (OrderedType ballMan : selected) {
			doPerform(ballMan, target);
		}
	}

	protected abstract void doPerform(OrderedType ballMan, Vector2f location);

	protected abstract void doPerform(OrderedType ballMan, WithNode target);

	public void setSelected(List<OrderedType> selected) {
		this.selected = selected;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
