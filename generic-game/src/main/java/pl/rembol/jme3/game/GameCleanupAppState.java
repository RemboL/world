package pl.rembol.jme3.game;

import com.jme3.app.state.AbstractAppState;

public class GameCleanupAppState extends AbstractAppState {

    private final GenericGameState gameState;

    public GameCleanupAppState(GenericGameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void cleanup() {
        super.cleanup();

        if (gameState != null) {
            gameState.threadManager.tearDown();
        }
    }
}
