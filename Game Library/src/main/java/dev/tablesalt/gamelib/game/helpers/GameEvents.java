package dev.tablesalt.gamelib.game.helpers;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

public class GameEvents {

    protected final Game game;

    protected GameEvents(Game game) {
        this.game = game;
    }

    protected void onChat(Player player,AsyncChatEvent event) {

    }

    protected void onDeath(Player player, EntityDamageEvent event) {

    }

    protected void onRespawn(Player player, PlayerRespawnEvent event) {

    }

    protected void onCommand(Player player, PlayerCommandPreprocessEvent event) {

    }

    protected void onInteract(Player player,PlayerInteractEvent event) {

    }

    protected void onEntityClick(Player player, PlayerInteractEntityEvent event) {

    }

    protected void onPvP(Player attacker, Player victim, EntityDamageByEntityEvent event) {

    }

    protected void onPvE(Player attacker, LivingEntity victim, EntityDamageByEntityEvent event) {

    }

    protected void onPlayerDamagedByEntity(Entity attacker, Player victim, EntityDamageByEntityEvent event) {

    }

    protected void onDamaged(Player victim, EntityDamageEvent event) {

    }

    protected void onPlayerKill(Player killer, LivingEntity victim, EntityDeathEvent event) {

    }

    protected void onPlayerKillPlayer(Player killer, Player victim) {

    }

    protected void onItemSpawn(Item item, ItemSpawnEvent event) {

    }

    protected void onBlockPlace(Player player, Block block, BlockPlaceEvent event) {

    }

    protected void onBlockBreak(Player player, Block block, BlockBreakEvent event) {

    }

    protected Game getGame() {
        return game;
    }




}
