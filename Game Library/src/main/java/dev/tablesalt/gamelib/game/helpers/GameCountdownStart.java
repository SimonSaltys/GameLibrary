package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.enums.GameState;
import dev.tablesalt.gamelib.game.enums.State;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.model.Countdown;
import org.mineacademy.fo.model.SimpleTime;

/**
 * The countdown timer for the start of the game,
 * only happens when transitioning from lobby to playing
 */
public class GameCountdownStart extends Countdown {
    private final Game game;
    public GameCountdownStart(Game game) {
        super(game.getLobbyDuration());
        this.game = game;
    }

    @Override
    protected void onTick() {
    }

    @Override
    protected void onTickError(Throwable t) {
    }

    @Override
    protected void onEnd() {
       game.getStarter().beginPlaying();
    }
}
