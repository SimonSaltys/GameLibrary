package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.enums.GameState;
import dev.tablesalt.gamelib.game.enums.State;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.exception.FoException;

public class Starter {
    @Getter
    private final Game game;

    private final GameCountdownStart startCountdown;


    public Starter(Game game) {
        this.game = game;
        this.startCountdown = new GameCountdownStart(game);
    }



    /*----------------------------------------------------------------*/
    /* OVERRIDABLE LOGIC */
    /*----------------------------------------------------------------*/

   protected void onGameStart() {}

    protected void onGameStartFor(Player player) {}

    protected boolean startGameWithCounter() { return true; }

    //starts the game and skips the countdown
    protected final void beginPlaying() {
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

    /**
     * Starts the game if stopped based on the first player to joins mode,
     * If there is more than one player and the conditions are met from {@link #canStart()}
     * then we will start the countdown of the game
     *
     * @param mode mode of the player who first joins, irrelevant when there is more than 1 player.
     */
    public final void startBasedOnMode(GameJoinMode mode) {

        if (game.getState().isStopped()) {
            if (mode == GameJoinMode.EDITING) {
                game.getState().setState(GameState.EDITED);
                game.scoreboard.onEditStart();
            } else {
                game.getState().setState(GameState.LOBBY);
                game.scoreboard.onLobbyStart();
            }
        }

        if (game.getState().isLobby() && canStart())
            if (startGameWithCounter())
                startCountdownToPlay();
            else
                beginPlaying();
    }

    private void startCountdownToPlay() {
        if (isStarting() || !game.getState().isLobby() || !canStart()) {
            Common.error(new FoException(), "Cannot start the countdown for " + game.getName() +
                    "stopping for safety");
            game.getStopper().stop();
            return;
        }

        startCountdown.launch();
    }

    public final boolean isStarting() {
        return startCountdown.isRunning();
    }

    public final void stopIfStarting() {
        if (isStarting())
            startCountdown.cancel();
    }



    public final int getTimeLeft() {
        return startCountdown.getTimeLeft();
    }

    protected boolean canStart() {
        return game.getPlayersInGame().size() >= game.getMinPlayers();
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
