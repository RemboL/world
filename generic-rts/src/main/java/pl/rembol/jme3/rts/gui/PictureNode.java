package pl.rembol.jme3.rts.gui;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import pl.rembol.jme3.game.gui.Clickable;
import pl.rembol.jme3.geom.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class PictureNode extends Node implements Clickable {

    @Override
    public void onClick() {
        onClickCallbacks.forEach(Callback::call);
    }

    @Override
    public boolean isClicked(Vector2f cursorPosition) {
        return  picture.getWorldTranslation().x <= cursorPosition.x &&
                picture.getWorldTranslation().x + picture.getWorldScale().x >= cursorPosition.x &&
                picture.getWorldTranslation().y <= cursorPosition.y &&
                picture.getWorldTranslation().y + picture.getWorldScale().y >= cursorPosition.y;
    }

    @FunctionalInterface
    public interface Callback {
        void call();
    }

    private final List<Callback> onClickCallbacks = new ArrayList<>();

    public final Picture picture;

    public PictureNode(Picture picture) {
        this.picture = picture;
        attachChild(picture);
    }

    public Vector2i getSize() {
        return new Vector2i((int) picture.getWorldScale().x, (int) picture.getWorldScale().y);
    }

    public void addOnClickListener(Callback callback) {
        onClickCallbacks.add(callback);
    }
}
