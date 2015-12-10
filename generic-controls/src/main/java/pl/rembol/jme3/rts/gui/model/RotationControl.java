package pl.rembol.jme3.rts.gui.model;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class RotationControl extends AbstractControl{

    private final ModelViewer modelViewer;

    public RotationControl(ModelViewer modelViewer) {
        this.modelViewer = modelViewer;
    }

    @Override
    protected void controlUpdate(float v) {
        modelViewer.rotate(v);
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
    }
}
