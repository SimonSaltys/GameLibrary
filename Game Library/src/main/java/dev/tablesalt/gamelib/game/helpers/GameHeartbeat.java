package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.helpers.Game;
import lombok.RequiredArgsConstructor;
import org.mineacademy.fo.model.Countdown;
import org.mineacademy.fo.model.SimpleTime;

/**
 * The main heartbeat of the game, put anything you want
 * to be repeating/updating in the onTick method of this class
 */
public class GameHeartbeat extends Countdown {

    private final Game game;

    public GameHeartbeat(Game game) {
        super(SimpleTime.fromSeconds(10));

        this.game = game;
    }

    @Override
    protected void onTick() {

    }

    @Override
    protected void onEnd() {
    }

    public void stopIfBeating() {
        if (isRunning())
            cancel();
    }
}
