package pl.rembol.jme3.rts.gui.console;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

import java.util.Objects;

class ConsoleLogLine extends AbstractControl {

    private BitmapText textLine;

    private float timeToLive;

    private static final float FADEOUT_TIME = 2;

    ConsoleLogLine(AssetManager assetManager, Node node, String text) {

        node.getChildren().stream()
                .map(spatial -> spatial.getControl(ConsoleLogLine.class))
                .filter(Objects::nonNull)
                .forEach(ConsoleLogLine::shiftUp);

        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        textLine = new BitmapText(guiFont);
        textLine.setSize(guiFont.getCharSet().getRenderedSize());
        textLine.setText(text);
        node.attachChild(textLine);
        textLine.addControl(this);
        timeToLive = 10f;
    }

    private void shiftUp() {
        textLine.move(0, textLine.getHeight(), 0);
    }

    @Override
    protected void controlUpdate(float tpf) {
        timeToLive -= tpf;

        if (timeToLive < FADEOUT_TIME && timeToLive > 0) {
            ColorRGBA color = new ColorRGBA(1f, 1f, 1f, 0f);
            color.interpolateLocal(ColorRGBA.White, timeToLive / FADEOUT_TIME);
            textLine.setColor(color);
        }

        if (timeToLive < 0) {
            textLine.removeControl(this);
            textLine.getParent().detachChild(textLine);
        }
    }

    @Override
    protected void controlRender(RenderManager paramRenderManager,
                                 ViewPort paramViewPort) {
    }
}
