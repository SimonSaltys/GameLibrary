package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.enums.State;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.map.GameMap;
import dev.tablesalt.gamelib.game.utils.MessageUtil;
import dev.tablesalt.gamelib.players.PlayerCache;
import dev.tablesalt.gamelib.players.helpers.PlayerStateIdentifier;
import dev.tablesalt.gamelib.tools.RegionTool;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.exception.FoException;

@RequiredArgsConstructor
public class PlayerJoiner {
    private final Game game;


    protected void onGameJoin(Player player) {

    }

    public final boolean joinPlayer(Player player, GameJoinMode mode) {
        PlayerCache cache = PlayerCache.from(player);

        if (!this.canJoin(cache,mode))
            return false;

        boolean success = preparePlayer(cache,mode);

        if (!success)
            return false;

        game.addPlayer(cache);
        game.getStarter().startBasedOnMode(mode);
        broadcastJoinMessage(player);

        game.scoreboard.onPlayerJoin(player);
        GameUtil.checkIntegrity(game);
        return true;
    }


    /*----------------------------------------------------------------*/
    /* OVERRIDABLE LOGIC */
    /*----------------------------------------------------------------*/
    protected boolean canJoinExtendedLogic() {
        return true;
    }

    protected boolean cleanPlayerOnJoin() {
        return true;
    }

    /*----------------------------------------------------------------*/
    /* PRIVATE */
    /*----------------------------------------------------------------*/

    private boolean canJoin(PlayerCache cache, GameJoinMode joinMode) {
        final Player player = cache.toPlayer();
        final String name = game.getName();
        final GameMap map = game.getMapRotator().getCurrentMap();
        final State state = game.getState();


        //does the player have a game?
        if (cache.getGameIdentifier().hasGame()) {
            Messenger.error(player, "You are already " + cache.getMode().getLocalized() + " the game " +
                    cache.getGameIdentifier().getCurrentGame().getName() + ".");
            return false;
        }

        //is there a map to join?
        if (map == null) {
            Messenger.error(player,"Game " + name + " does not have a map, if you are an Admin please " +
                    "set one up.");
            return false;
        }

        if (state.isEdited() && map.getRegion().isWhole()) {
            Messenger.error(player,"Game " + name + " does not have a lobby point set. Please set one up.");
            return false;
        }

        assert player != null;
        //is the player dead?
        if (player.isDead()) {
            Messenger.error(player, "You cannot join game " + name + " while you are dead");
            return false;
        }


        //is the game being edited right now, and the player is trying to play?
        if (state.isEdited() && joinMode != GameJoinMode.EDITING) {
            Messenger.error(player, "Game " + name + " is being edited right now.");

            return false;
        }

        //don't let the player edit a playing game.
        if (state.isPlayed() && joinMode == GameJoinMode.EDITING) {
            Messenger.error(player, "Game " + name + " cannot be edited while it's being played.");

            return false;
        }

        //only let spectators join a game that is being played
        if (!state.isPlayed() && joinMode == GameJoinMode.SPECTATING) {
            Messenger.error(player, "Only games that are being played may be spectated.");
            return false;
        }

        if (state.isPlayed() && joinMode == GameJoinMode.PLAYING) {
            Messenger.error(player, "Game " +  name + " has already started. Type /game spectate " + name + " to observe.");
            return false;
        }

        //is the game ready to be played?
        if (!map.isSetup() && joinMode != GameJoinMode.EDITING) {
            Messenger.error(player, "Game " + name + " is not yet configured. If you are an admin, run '/game edit " + name + "' to see what's missing.");

            return false;
        }

        //is the game full?
        if (state.isPlayed() && joinMode == GameJoinMode.PLAYING && game.getPlayerGetter().getPlayers(GameJoinMode.PLAYING).size() >= game.getMaxPlayers()) {
            Messenger.error(player, "Arena " + name + " is full (" + game.getMaxPlayers() + " players)!");
            return false;
        }

        return canJoinExtendedLogic();
    }

    private boolean preparePlayer(PlayerCache cache, GameJoinMode mode) {
        Player player = cache.toPlayer();
        cache.getTagger().clearTags();
        cache.getStateIdentifier().setPlayerState(PlayerStateIdentifier.PlayerState.ALIVE);


        if (mode != GameJoinMode.EDITING) {
            GameUtil.teleport(player, game.getMapRotator().getCurrentMap().getLobbyRegion().getCenter());

            PlayerUtil.normalize(player,cleanPlayerOnJoin());
        }

        try {
            onGameJoin(player);
        } catch (Throwable t) {
            Common.error(t,"Failed to properly handle " + player.getName() + " joining to game" + game.getName());
            return false;
        }

        cache.setGameJoinMode(mode);
        cache.getGameIdentifier().setCurrentGame(game);

        return true;
    }

    private void broadcastJoinMessage(Player player) {
        game.getGameBroadcaster().broadcast("&6" + player.getName() + " &7has joined the game! " +
                "(" + game.getPlayersInGame().size() + "/" + game.getMaxPlayers() + ")");

    }
}
