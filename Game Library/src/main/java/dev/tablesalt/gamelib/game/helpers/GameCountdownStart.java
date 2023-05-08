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
        beginPlaying();
    }

    private void beginPlaying() {
        String name = game.getName();
        State state = game.getState();

        Valid.checkBoolean(state.isLobby(),"Cannot start game " + name + " while in the " + state + " mode");
        state.setState(GameState.PLAYED);

        try {
            game.getHeartbeat().launch();
            game.getScoreboard().onGameStart();
            game.getStarter().onGameStart();

            closeAllInventories();
            startGameForAll();

        } catch (Throwable t) {
            Common.throwError(t,"Failed to start game " + name + " stopping for safety");
            game.getStopper().stop();
        }
    }

    private void closeAllInventories() {
        game.getPlayerGetter().forEachInAllModes(cache -> {
            Player player = cache.toPlayer();
            Valid.checkNotNull(player,"Found null player in game " + game.getName() + " while starting");
            player.closeInventory();
        });
    }

    private void startGameForAll() {
        game.getPlayerGetter().forEachInAllModes(cache -> {
            Player player = cache.toPlayer();
            Valid.checkNotNull(player,"Found null player in game " + game.getName() + " while starting");
            game.getStarter().onGameStartFor(player);
        });
    }


}
