package pl.rembol.jme3.world.hud;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.jme3.ui.Picture;

public class BlinkControl extends AbstractControl {

    float timeToLive = 1;
    private Picture picture;
    private Material material;
    private Vector3f startPosition;
    private Vector3f finishPosition;
    private float size;

    public BlinkControl(Picture picture, float size) {
        if (picture.getControl(BlinkControl.class) == null) {
            this.picture = picture;
            this.material = picture.getMaterial();
            this.size = size;

            startPosition = picture.getLocalTranslation().clone()
                    .add(-size / 2, -size / 2, 0);
            finishPosition = picture.getLocalTranslation().clone();

            picture.addControl(this);
        }
    }

    @Override
    protected void controlRender(RenderManager paramRenderManager,
                                 ViewPort paramViewPort) {
    }

    @Override
    protected void controlUpdate(float tpf) {
        timeToLive -= tpf;

        if (timeToLive > 0) {
            ColorRGBA color = new ColorRGBA();
            color.interpolate(ColorRGBA.White, ColorRGBA.Red, timeToLive);
            material.setColor("Color", color);

            Vector3f position = new Vector3f();
            position.interpolate(finishPosition, startPosition, timeToLive);
            picture.setLocalTranslation(position);

            float scale = size * (1 + timeToLive);
            picture.setLocalScale(new Vector3f(scale, scale, 1f));
        } else {
            material.setColor("Color", ColorRGBA.White);
            picture.setLocalTranslation(finishPosition);
            picture.setLocalScale(new Vector3f(size, size, 1f));

            picture.removeControl(this);
        }

    }
}
