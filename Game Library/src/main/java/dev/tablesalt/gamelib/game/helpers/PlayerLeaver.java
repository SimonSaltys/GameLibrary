package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.utils.Message;
import dev.tablesalt.gamelib.players.PlayerCache;
import dev.tablesalt.gamelib.players.helpers.GameIdentifier;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.Valid;

@RequiredArgsConstructor
public class PlayerLeaver {

    private final Game game;

    protected void onGameLeave(Player player) {

    }

    public final void leavePlayer(Player player, Message message) {
        PlayerCache cache = PlayerCache.from(player);
        String name = game.getName();
        GameIdentifier identifier = cache.getGameIdentifier();

        Valid.checkBoolean(!game.getState().isStopped(), "Cannot leave player " + player.getName()
                + "from stopped game " + game.getName() + "!");

        Valid.checkBoolean(identifier.hasGame() && identifier.getCurrentGame().equals(game),
                "Player " + player.getName() + "is not joined in game " + name);

        try {
            tryToLeavePlayerSafely(player);
            // TODO: 3/30/2023 Teleport player to hub when leave, or to their previous location if no hub server support

            if (game.getPlayersInGame().isEmpty())
                game.getStopper().stop();
            else {

                if (message != null)
                    game.getGameBroadcaster().broadcastInfo(message.getMessage());
                else if(game.getState().isLobby()) {
                    game.getGameBroadcaster().broadcast("&6" + player.getName() + " &7has left the game! " +
                            "(" + game.getPlayerGetter().getPlayers(GameJoinMode.PLAYING).size() + "/" + game.getMaxPlayers() + ")");
                    Messenger.success(player, "You've left " + cache.getMode().getLocalized() + " the game " + name + "!");
                }
            }
        } finally {
            cache.reset();
        }

    }


    /*----------------------------------------------------------------*/
    /* PRIVATE */
    /*----------------------------------------------------------------*/

    private void tryToLeavePlayerSafely(Player player) {
        PlayerCache cache = PlayerCache.from(player);

        game.scoreboard.onPlayerLeave(player);
        game.removePlayer(cache);

        callOnGameLeaveFor(player);
    }

    private void callOnGameLeaveFor(Player player) {
        try {
            onGameLeave(player);
        } catch (final Throwable t) {
            Common.error(t, "failed to properly handle " + player.getName() + " leaving game " + game.getName());

            if (!game.getState().isStopped()) {
                game.getStopper().stop();
            }
        }
    }
}

