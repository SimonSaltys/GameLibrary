package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.enums.GameState;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.exception.FoException;

public class Starter {
    @Getter
    private final Game game;

    private final GameCountdownStart startCountdown;


    public Starter(Game game) {
        this.game = game;
        this.startCountdown = new GameCountdownStart(game);
    }

   protected void onGameStart() {

    }

    protected void onGameStartFor(Player player) {

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
            startCountdownToPlay();
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

    private boolean canStart() {
        return game.getPlayersInGame().size() >= game.getMinPlayers();
    }


}
