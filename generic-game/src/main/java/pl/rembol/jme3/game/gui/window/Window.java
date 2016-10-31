package pl.rembol.jme3.game.gui.window;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import pl.rembol.jme3.game.GenericGameState;
import pl.rembol.jme3.game.gui.Clickable;
import pl.rembol.jme3.game.input.MouseInputManager;
import pl.rembol.jme3.geom.Rectangle2f;
import pl.rembol.jme3.utils.Spatials;

import java.util.Optional;

abstract public class Window<GS extends GenericGameState> extends Node implements AnalogListener, ActionListener {
    
    protected GS gameState;
    
    private Geometry shade;
    
    protected Vector2f size;

    public Window(GS gameState, String name) {
        this(gameState, name, new Vector2f(gameState.settings.getWidth(), gameState.settings.getHeight()));
    }
    
    public Window(GS gameState, String name, Vector2f size) {
        super(name);
        
        this.gameState = gameState;
        this.size = size;
        createShade();
        
    }

    private void createShade() {
        if (shade != null) {
            detachChild(shade);
        }
        
        shade = new Geometry(name, new Quad(size.x, size.y));
        Material material = new Material(gameState.assetManager, "Common/MatDefs/Gui/Gui.j3md");
        material.setColor("Color", new ColorRGBA(0f, 0f, 1f, .5f));
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        shade.setQueueBucket(RenderQueue.Bucket.Gui);
        shade.setCullHint(CullHint.Never);
        shade.setMaterial(material);

        attachChild(shade);
    }

    public Integer getTopOffset() {
        return (int) getLocalTranslation().getZ() + 100;
    }

    public void close() {
         gameState.windowManager.closeWindow(this);
    }
    
    protected void resize(Vector2f size) {
        this.size = size;
        createShade();
    }

    private Optional<Clickable> findClickedChild(Vector2f cursorPosition) {
        return Spatials.getDescendants(this)
                .filter(Clickable.class::isInstance)
                .map(Clickable.class::cast)
                .filter(clickable -> clickable.isClicked(cursorPosition))
                .findFirst();
    }

    public void click(Vector2f cursorPosition) {
        Optional<Clickable> clicked = findClickedChild(cursorPosition);

        if (clicked.isPresent()) {
            clicked.get().onClick();
        } else {
            if (!new Rectangle2f(
                    new Vector2f(getLocalTranslation().x, getLocalTranslation().y),
                    new Vector2f(getLocalTranslation().x + size.x, getLocalTranslation().y + size.y))
                    .isInside(cursorPosition)) {
                onClickOutside();
            }
        }
    }

    protected void onClickOutside() {
        close();
    }

    public void onAnalog(String name, float value, float tpf) {

    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(MouseInputManager.LEFT_CLICK) && isPressed) {
            click(gameState.inputManager.getCursorPosition().clone());
        }
    }

}
