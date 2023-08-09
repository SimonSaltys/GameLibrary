package dev.tablesalt.gamelib.players.helpers;

import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.RequiredArgsConstructor;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.exception.FoException;
@RequiredArgsConstructor
public class GameIdentifier {

    private final PlayerCache cache;

    public boolean hasGame() {
        if ((cache.getCurrentGameName() != null && cache.getMode() == null) ||
                (cache.getCurrentGameName() == null && cache.getMode() != null))

            throw new FoException("Current game and current game mode must both be set or both be null, "
                    + cache.getPlayerName() + " had game " + cache.getCurrentGameName() + " with mode " + cache.getMode());

        return cache.getCurrentGameName() != null;
    }
    public Game getCurrentGame() {
        if (this.hasGame()) {
            final Game game = Game.findByName(cache.getCurrentGameName());

            Valid.checkNotNull(game, "Found player " + cache.getPlayerName() + " having unloaded game " +
                    cache.getCurrentGameName() + " in their cache");

            return game;
        }
        return null;
    }

    public boolean currentGameEquals(Game game) {
        return hasGame() && getCurrentGame().equals(game);
    }

    public void setCurrentGame(Game game) {
        cache.setCurrentGameName(game.getName());
    }
}
