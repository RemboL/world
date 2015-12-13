package pl.rembol.jme3.rts.gui;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public interface Clickable {

    void onClick();

    boolean isClicked(Vector2f cursorPosition);

    default boolean isClicked(Vector2f cursorPosition, Vector3f pictureStart, Vector3f pictureSize) {
        return pictureStart.x <= cursorPosition.x &&
                pictureStart.x + pictureSize.x >= cursorPosition.x &&
                pictureStart.y <= cursorPosition.y &&
                pictureStart.y + pictureSize.y >= cursorPosition.y;
    }

}
