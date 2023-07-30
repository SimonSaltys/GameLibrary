package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.utils.Message;
import dev.tablesalt.gamelib.players.PlayerCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.annotation.AutoRegister;

@AutoRegister
public final class GameListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Game game = Game.findByLocation(player.getLocation());

        PlayerCache cache = PlayerCache.from(player);
        cache.load();

        if(game == null) {
            return;
        }

        Valid.checkBoolean(!game.getPlayersInGame().contains(cache), "Found disconnected player " +
                player.getName() + " in game region " + game.getName() + " while connecting to server.");

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerCache cache = PlayerCache.from(player);

        Game game = cache.getGameIdentifier().getCurrentGame();

        if (game == null) {
            return;
        }

        game.getPlayerLeaver().leavePlayer(player, Message.NO_MESSAGE);
        cache.save();
        cache.removeFromMemory();
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent event) {
        if (event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED))
            event.setCancelled(true);
    }
}
