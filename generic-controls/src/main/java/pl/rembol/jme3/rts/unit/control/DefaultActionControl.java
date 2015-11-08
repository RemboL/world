package pl.rembol.jme3.rts.unit.control;

import com.jme3.math.Vector2f;
import com.jme3.scene.control.Control;
import pl.rembol.jme3.rts.unit.interfaces.WithNode;

public interface DefaultActionControl extends Control {

    void performDefaultAction(WithNode target);

    void performDefaultAction(Vector2f target);

}
