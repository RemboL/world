package pl.rembol.jme3.world.hud.status;

import java.util.ArrayList;
import java.util.List;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import pl.rembol.jme3.world.GameState;

abstract public class DefaultStatus extends Node {

    protected final GameState gameState;

    private List<BitmapText> statusText = new ArrayList<>();

    public DefaultStatus(GameState gameState) {
        this.gameState = gameState;

        initStatusLines();
    }

    abstract public void update();

    protected void updateText(String... lines) {
        for (BitmapText line : statusText) {
            line.setText("");
        }

        for (int i = 0; i < lines.length && i < statusText.size(); ++i) {
            statusText.get(i).setText(lines[i]);
        }
    }

    private void initStatusLines() {
        BitmapFont guiFont = gameState.assetManager.loadFont("Interface/Fonts/Default.fnt");

        for (int i = 0; i < getTextLineNumber(); ++i) {
            BitmapText textLine = new BitmapText(guiFont);
            textLine.setSize(guiFont.getCharSet().getRenderedSize());
            attachChild(textLine);
            textLine.move(50, 80 + (1 - i) * textLine.getLineHeight(), 0);
            statusText.add(textLine);
        }
    }

    abstract protected int getTextLineNumber();

}
