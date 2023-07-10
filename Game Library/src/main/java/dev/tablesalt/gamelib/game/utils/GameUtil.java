package dev.tablesalt.gamelib.game.utils;

import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.enums.GameState;
import dev.tablesalt.gamelib.game.enums.State;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.fo.visual.VisualizedRegion;

import java.util.List;

@UtilityClass
public class GameUtil {
    public void teleport(final Player player, @NonNull final Location location) {
        Valid.checkBoolean(player != null && player.isOnline(), "Cannot teleport offline players!");
        Valid.checkBoolean(!player.isDead(), "Cannot teleport dead player " + player.getName());


        final Location topOfTheBlock = location.getBlock().getLocation().add(0.5, 1, 0.5);

        final boolean success = player.teleport(topOfTheBlock, PlayerTeleportEvent.TeleportCause.PLUGIN);
        Valid.checkBoolean(success, "Failed to teleport " + player.getName() + " to both primary and " +
                "fallback location, they may get stuck in the game region!");
    }




    public void checkIntegrity(Game game) {
        String name = game.getName();
        State state = game.getState();
        List<PlayerCache> players = game.getPlayersInGame().getSource();


        if (state.isStopped())
            Valid.checkBoolean(players.isEmpty(), "Found players in stopped " + name + " game");

        int playing = 0, editing = 0, spectating = 0;

        for (final PlayerCache cache : players) {
            final Player player = cache.toPlayer();
            final GameJoinMode mode = cache.getMode();

            Valid.checkBoolean(player != null && player.isOnline(), "Found a disconnected player " + player + " in game " + name);

            if (mode == GameJoinMode.PLAYING)
                playing++;

            else if (mode == GameJoinMode.EDITING)
                editing++;

            else if (mode == GameJoinMode.SPECTATING)
                spectating++;
        }


        if (editing > 0) {
            Valid.checkBoolean(state.isEdited(), "Game " + name
                    + " must be in EDIT mode not " + state + " while there are " + editing + " players");

            Valid.checkBoolean(playing == 0 && spectating == 0, "Found " + playing + " and "
                    + spectating + " players in an edited game " + name);
        }
    }

    public String generateColoredGradientNumerical(int maxValue, int value) {
        return (value >= maxValue ? "&a" : value >= maxValue / 2 ? "&6" : "&4") + value + "/" + maxValue;
    }
}
