package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.utils.GameUtil;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;

/**
 * Used to send messages to players that are in
 * the given game
 */
public final class GameBroadcaster {

    private final Game game;
    private final PlayerGetter playerGetter;

    GameBroadcaster(Game game) {
        this.game = game;
        this.playerGetter = game.getPlayerGetter();
    }

    public void broadcast(String message) {
        GameUtil.checkIntegrity(game);

        playerGetter.forEachInAllModes(player -> Common.tellNoPrefix(player.toPlayer(),message));
    }

    public void broadcastInfo(String message) {
        GameUtil.checkIntegrity(game);

        playerGetter.forEachInAllModes(player -> Messenger.info(player.toPlayer(),message));

    }




}
