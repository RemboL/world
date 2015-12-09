package pl.rembol.jme3.rts.gui.window;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gui.ClickablePicture;

public class AddButton extends ClickablePicture {

    public AddButton(GameState gameState) {
        super(gameState);
        this.setName("add button");
        setImage(gameState.assetManager, "interface/" + gameState.themeName() + "/add_button.png", true);
        setWidth(SIZE);
        setHeight(SIZE);
    }

    @Override
    public void onClick() {
        gameState.windowManager.addWindow(new Window(gameState, 640, 480), new Vector2f(FastMath.nextRandomFloat() * 200 + 10, FastMath.nextRandomFloat() * 200 + 10));
    }
}
