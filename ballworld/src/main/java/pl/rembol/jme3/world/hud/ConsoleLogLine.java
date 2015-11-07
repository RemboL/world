package pl.rembol.jme3.world.hud;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.world.GameState;

public class ConsoleLogLine extends AbstractControl {

    private BitmapText textLine;

    private float timeToLive;

    public ConsoleLogLine(GameState gameState, Node node, String text) {

        for (Spatial spatial : node.getChildren()) {
            if (spatial.getControl(ConsoleLogLine.class) != null) {
                spatial.getControl(ConsoleLogLine.class).shiftUp();
            }
        }

        BitmapFont guiFont = gameState.assetManager
                .loadFont("Interface/Fonts/Default.fnt");
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

        if (timeToLive < 2 && timeToLive > 0) {
            ColorRGBA color = new ColorRGBA(1f, 1f, 1f, 0f);
            color.interpolate(ColorRGBA.White, timeToLive / 2);
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
