package pl.rembol.jme3.rts.controls;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.utils.Booleans;

public class FadeOutControl extends AbstractControl {

    private static final float SECONDS_TO_FULL_FADE_OUT = 2f;

    private boolean initialized = false;

    private Set<Material> materialList;

    private void init() {
        getSpatial().setQueueBucket(RenderQueue.Bucket.Transparent);

        materialList = getMaterialsFromSpatial(getSpatial()).collect(Collectors.toSet());

        materialList.stream().forEach(
                material -> material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha));

        initialized = true;
    }

    @Override
    protected void controlUpdate(float v) {
        if (!initialized) {
            init();
        }

        boolean isTransparent = materialList.stream()
                .map(material -> Stream
                        .of("Color", "Specular", "Ambient", "Diffuse")
                        .filter(colorName -> material.getParam(colorName) != null)
                        .map(colorName -> {
                            MatParam matParam = material.getParam(colorName);
                            if (matParam == null) {
                                return true;
                            } else {
                                ColorRGBA color = (ColorRGBA) matParam.getValue();
                                ColorRGBA newColor = new ColorRGBA(color.getRed(), color.getGreen(), color.getBlue(),
                                        Math.max(0, color.getAlpha() - v / SECONDS_TO_FULL_FADE_OUT));
                                material.setColor(colorName, newColor);

                                return newColor.getAlpha() == 0;
                            }
                        })
                        .reduce(Booleans.OR_REDUCTOR)
                        .orElse(true))
                .reduce(Booleans.OR_REDUCTOR)
                .orElse(true);
        if (isTransparent) {
            getSpatial().getParent().detachChild(getSpatial());
        }
    }

    private Stream<Material> getMaterialsFromSpatial(Spatial spatial) {
        if (spatial instanceof Node) {
            return ((Node) spatial).getChildren().stream().flatMap(this::getMaterialsFromSpatial);
        } else if (spatial instanceof Geometry) {
            return Stream.of(((Geometry) spatial).getMaterial());
        }

        return Stream.empty();
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {

    }
}
