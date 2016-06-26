package pl.rembol.jme3.rts.gui.window.text;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gui.window.Window;

import java.util.ArrayList;
import java.util.List;

public class TextWindow extends Window {

    private List<BitmapText> textList = new ArrayList<>();

    public TextWindow(GameState gameState, int width, int height) {
        super(gameState, width, height);
    }

    public void addText(String text) {
        BitmapFont guiFont = gameState.assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText textLine = new BitmapText(guiFont);
        textLine.setSize(guiFont.getCharSet().getRenderedSize());
        textLine.setText(text);

        float currentHeight = textList.stream()
                .map(BitmapText::getLineHeight)
                .reduce(0F, (f1, f2) -> f1 + f2);

        textLine.setLocalTranslation(
                getInnerFrameTopLeftCorner().x,
                getInnerFrameTopLeftCorner().y - currentHeight,
                1);
        this.attachChild(textLine);
        textList.add(textLine);
    }


}
