package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.map.GameMap;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import dev.tablesalt.gamelib.game.utils.Message;
import dev.tablesalt.gamelib.players.PlayerCache;
import dev.tablesalt.gamelib.players.helpers.PlayerTagger;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.EntityUtil;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.event.RocketExplosionEvent;
import org.mineacademy.fo.exception.EventHandledException;
import org.mineacademy.fo.remain.Remain;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class GameListener implements Listener {

    /**
     * The entities we prevent clicking, placing or removing even if the arena is played
     */
    private static final Set<String> ENTITY_TYPE_MANIPULATION_BLACKLIST = Common.newSet("ITEM_FRAME", "PAINTING", "ARMOR_STAND", "LEASH_HITCH");

    /*
     * Register a class containing events not available in older Minecraft versions
     */
    private void registerEvent(String classPath, Supplier<Listener> listener) {
        this.registerEvent(classPath, listener, null, null);
    }

    public GameListener() {
        registerEvent("org.bukkit.event.player.PlayerItemConsumeEvent", ConsumeItemListener::new);
        registerEvent("org.bukkit.event.player.PlayerBucketEntityEvent", PlayerBucketEntityListener::new,
                "org.bukkit.event.player.PlayerBucketFishEvent", PlayerBucketFishListener::new);

        registerEvent("org.bukkit.event.player.PlayerTakeLecternBookEvent", PlayerTakeLecternBookListener::new);

        registerEvent("org.bukkit.event.entity.EntityEnterBlockEvent", EntityEnterBlockListener::new);
        registerEvent("org.bukkit.event.entity.EntityPickupItemEvent", EntityPickupItemListener::new,
                "org.bukkit.event.player.PlayerPickupItemEvent", PlayerPickupItemListener::new);

        registerEvent("org.bukkit.event.entity.EntityBreedEvent", EntityBreedListener::new);
        registerEvent("org.bukkit.event.entity.SpawnerSpawnEvent", SpawnerSpawnListener::new);

        registerEvent("org.bukkit.event.block.BlockReceiveGameEvent", BlockReceiveGameListener::new);
        registerEvent("org.bukkit.event.block.BlockExplodeEvent", ExplodeAndEntitySpawnListener::new);
        registerEvent("org.bukkit.event.block.CauldronLevelChangeEvent", CauldronLevelChangeListener::new);

        registerEvent("org.bukkit.event.raid.RaidTriggerEvent", RaidTriggerListener::new);
    }

    /*
     * Register a class containing events not available in older Minecraft versions
     */
    private void registerEvent(String classPath, Supplier<Listener> listener, @Nullable String fallbackClass, @Nullable Supplier<Listener> fallbackListener) {
        try {
            Class.forName(classPath);

            Common.registerEvents(listener.get());

        } catch (final ClassNotFoundException ex) {
            if (fallbackListener != null)
                try {
                    Class.forName(fallbackClass);

                    Common.registerEvents(fallbackListener.get());
                } catch (final Throwable t) {
                    // Completely unavailable
                }
        }
    }

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
    public void onChat(AsyncChatEvent event) {
        final Player player = event.getPlayer();
        final PlayerCache cache = PlayerCache.from(player);

        if (cache.getGameIdentifier().hasGame())
            try {
               getEvents(player).onChat(player,event);

            } catch (EventHandledException ex) {
                event.setCancelled(ex.isCancelled());
            }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerCache cache = PlayerCache.from(player);
        PlayerTagger tagger = cache.getTagger();

        if (cache.getGameIdentifier().hasGame())
            try {
                tagger.setPlayerTag("Respawning", event);

                cache.getGameIdentifier().getCurrentGame().getGameEvents().onRespawn(player,event);

                if (tagger.hasPlayerTag("Respawning"))
                    tagger.removePlayerTag("Respawning");

            } catch (EventHandledException ex) {
                // Handled upstream
            }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        PlayerCache cache = PlayerCache.from(player);

        if (cache.getGameIdentifier().hasGame())
            try {

                getEvents(player).onCommand(player,event);

            } catch (EventHandledException ex) {
                event.setCancelled(ex.isCancelled());
            }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Game game = Game.findByLocation(event.hasBlock() ? event.getClickedBlock().getLocation() : player.getLocation());

        if (game != null) {
            PlayerCache cache = PlayerCache.from(player);

           if (!cache.getGameIdentifier().currentGameEquals(game)) {
               if (!action.toString().contains("AIR") && action != Action.PHYSICAL && !Remain.isInteractEventPrimaryHand(event))
                   Messenger.warn(player, "Use '/game edit' to make changes to this game.");

               event.setCancelled(true);
               player.updateInventory();
           } else {
               try {
                   getEvents(player).onInteract(player,event);
               } catch (EventHandledException ex) {
                   //Handled upstream
               }
           }
        }
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity entity = event.getRightClicked();
        final Game game = Game.findByLocation(entity.getLocation());

        if (game != null) {
            PlayerCache cache = game.getPlayerGetter().getPlayerInGame(player);

            if (cache == null || cache.getMode() == GameJoinMode.SPECTATING) {
                event.setCancelled(true);

                return;
            }

            try {
                game.getGameEvents().onEntityClick(player,event);

            } catch (final EventHandledException ex) {
                event.setCancelled(ex.isCancelled());
            }
        }
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        this.executeIfPlayingGame(event, (player, cache) -> event.setAmount(0));
    }

    @EventHandler
    public void onEntityAttacked(EntityDamageByEntityEvent event) {
        final Entity entityVictim = event.getEntity();
        final Entity entityAttacker = event.getDamager();

        final Game victimGame = Game.findByLocation(entityVictim.getLocation());
        final Game attackerGame = Game.findByLocation(entityAttacker.getLocation());

        if (victimGame == null || attackerGame == null) {
            event.setCancelled(true);
            return;
        }

        //stop players from hurting/killing certain entities
        if (ENTITY_TYPE_MANIPULATION_BLACKLIST.contains(entityVictim.getType().toString()) && !attackerGame.getState().isEdited()) {
            event.setCancelled(true);
            return;
        }

        if (!victimGame.equals(attackerGame)) {
            event.setCancelled(true);
            return;
        }

        if (victimGame.getState().isLobby() && victimGame.getMapRotator().getCurrentMap().getLobbyRegion().isWithin(entityVictim.getLocation())) {
            event.setCancelled(true);
            return;
        }

        if (entityAttacker instanceof Player playerAttacker) {
            //is the attacker in the same game as the victim?
            PlayerCache attackerCache = victimGame.getPlayerGetter().getPlayerInGame(playerAttacker);

            if (attackerCache == null || attackerCache.getMode() == GameJoinMode.SPECTATING) {
                event.setCancelled(true);
                return;
            }

            if (entityVictim instanceof Player playerVictim) {
                try {
                    victimGame.getGameEvents().onPvP(playerAttacker,playerVictim,event);

                    if (event.getFinalDamage() >= playerVictim.getHealth())
                        victimGame.getGameEvents().onPlayerKillPlayer(playerAttacker,playerVictim);

                } catch (final EventHandledException ex) {
                    event.setCancelled(ex.isCancelled());
                }

            } else if (entityVictim instanceof LivingEntity livingEntityVictim){
                try {

                    victimGame.getGameEvents().onPvE(playerAttacker,livingEntityVictim,event);

                } catch (final EventHandledException ex) {
                    event.setCancelled(ex.isCancelled());
                }
            }
        } else if (entityVictim instanceof Player playerVictim){
            PlayerCache cache = PlayerCache.from(playerVictim);

            if (cache.getMode() != GameJoinMode.PLAYING) {
                event.setCancelled(true);
                return;
            }

            try {
                victimGame.getGameEvents().onPlayerDamagedByEntity(entityAttacker,playerVictim,event);


            } catch (final EventHandledException ex) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityCombust(final EntityCombustEvent event) {
        this.cancelIfInGame(event, event.getEntity().getLocation());
    }

    @EventHandler
    public void onEntityInteract(final EntityInteractEvent event) {
        this.cancelIfInStoppedOrLobby(event, event.getEntity());
    }

    @EventHandler
    public void onEntityTarget(final EntityTargetEvent event) {
        final Entity from = event.getEntity();
        final Entity target = event.getTarget();

        final Game fromGame = Game.findByLocation(from.getLocation());

        // Prevent exp from being drawn into players
        if (from instanceof ExperienceOrb && fromGame != null) {
            from.remove();

            return;
        }

        final Game targetGame = target != null ? Game.findByLocation(target.getLocation()) : null;

        if (targetGame != null) {
            if (!targetGame.getState().isPlayed() && !targetGame.getState().isEdited())
                event.setCancelled(true);

            else if (fromGame == null || !fromGame.equals(targetGame))
                event.setCancelled(true);
        }

        if (target instanceof Player) {
            final PlayerCache cache = PlayerCache.from((Player) target);

            // Prevent players in editing or spectating mode from being targeted
            if (cache.getGameIdentifier().hasGame() && cache.getMode() != GameJoinMode.PLAYING)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player playerVictim))
            return;

        final PlayerCache cache = PlayerCache.from(playerVictim);
        final Game game = cache.getGameIdentifier().getCurrentGame();

        if (game == null) return;

        final GameMap map = game.getMapRotator().getCurrentMap();

        if (map == null) return;

       if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
           game.getGameEvents().onDeath(playerVictim,event);
           GameUtil.teleport(playerVictim, game.getMapRotator().getCurrentMap().getLobbyRegion().getCenter());
           event.setCancelled(true);
           return;
       }


        if (game.getState().isLobby() || map.getLobbyRegion().isWithin(playerVictim.getLocation()) || cache.getMode() == GameJoinMode.SPECTATING) {
                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    GameUtil.teleport(playerVictim, game.getMapRotator().getCurrentMap().getLobbyRegion().getCenter());
                }

                event.setCancelled(true);
                playerVictim.setFireTicks(0);
                return;
        }


            try {
                game.getGameEvents().onDamaged(playerVictim,event);

                if (event.getFinalDamage() >= playerVictim.getHealth()) {
                    game.getGameEvents().onDeath(playerVictim, event);
                    event.setCancelled(true);
                }

            }  catch (final EventHandledException ex) {
                // Handled
            }
        }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        final LivingEntity victim = event.getEntity();
        final Game game = Game.findByLocation(victim.getLocation());
        final Player killer = victim.getKiller();

        if (game == null || killer == null) return;

        final PlayerCache killerCache = PlayerCache.from(killer);
        if (killerCache.getMode() == GameJoinMode.PLAYING && killerCache.getGameIdentifier().currentGameEquals(game)) {
            try {
                game.getGameEvents().onPlayerKill(killer, victim, event);

            } catch (final EventHandledException ex) {
                // Handled
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        final Game game = Game.findByLocation(event.getLocation());

        if (game != null && !game.getState().isPlayed() && !game.getState().isEdited())
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemSpawn(final ItemSpawnEvent event) {
        this.cancelIfInStoppedOrLobby(event, event.getLocation());

        if (event.isCancelled())
            return;

        Item entity = event.getEntity();
        Game game = Game.findByLocation(entity.getLocation());

        if (game == null)
            //stops spectators or admins from dropping items outside the map bounds into the map for the players to get
            EntityUtil.trackFlying(entity, () -> {
                final Game gameInNewLocation = Game.findByLocation(entity.getLocation());

                if (gameInNewLocation != null)
                    entity.remove();
            });
        else
            try {
                game.getGameEvents().onItemSpawn(entity, event);

            } catch (EventHandledException ex) {
                event.setCancelled(ex.isCancelled());
            }
    }

    @EventHandler
    public void onBlockDispense(final BlockDispenseEvent event) {
        this.cancelIfInStoppedOrLobby(event, event.getBlock().getLocation());
    }
    @EventHandler
    public void onBucketFill(final PlayerBucketFillEvent event) {
        preventBucketGrief(event.getBlockClicked().getLocation(), event);
    }

    @EventHandler
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        preventBucketGrief(event.getBlockClicked().getLocation(), event);
    }

    private <T extends PlayerEvent & Cancellable> void preventBucketGrief(final Location location, final T event) {
        final Game game = Game.findByLocation(location);

        if (game != null) {
            final PlayerCache cache = game.getPlayerGetter().getPlayerInGame(event.getPlayer());

            if (cache == null || cache.getMode() != GameJoinMode.EDITING)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockForm(final BlockFormEvent event) {
        this.cancelIfInGame(event, event.getBlock().getLocation());
    }

    @EventHandler
    public void onBlockSpread(final BlockSpreadEvent event) {
        this.cancelIfInGame(event, event.getBlock().getLocation());
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        this.cancelIfInGame(event, event.getBlock().getLocation());
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        this.cancelIfInGame(event, event.getBlock().getLocation());
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        this.cancelIfInGame(event, event.getBlock().getLocation());
    }

    @EventHandler
    public void onDoorBreak(final EntityBreakDoorEvent event) {
        this.cancelIfInGame(event, event.getBlock().getLocation());
    }

    @EventHandler
    public void onEggThrow(final PlayerEggThrowEvent event) {
        final Egg egg = event.getEgg();
        final Game game = Game.findByLocation(egg.getLocation());

        // Prevent spawning chickens in arenas from eggs
        if (game != null)
            event.setHatching(false);
    }

    @EventHandler
    public void onPotionSplash(final PotionSplashEvent event) {
        preventProjectileGrief(event.getEntity());
    }

    @EventHandler
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        preventProjectileGrief(event.getEntity());
    }

    @EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {
        preventProjectileGrief(event.getEntity());
    }

    @EventHandler
    public void onRocketExplosion(final RocketExplosionEvent event) {
        preventProjectileGrief(event.getProjectile());
    }

    private void preventProjectileGrief(final Projectile projectile) {
        final Game game = Game.findByLocation(projectile.getLocation());

        if (game != null) {

            if (!game.getState().isPlayed() && !game.getState().isPlayed())
                projectile.remove();

            else if (projectile.getShooter() instanceof Player) {
                final PlayerCache cache = game.getPlayerGetter().getPlayerInGame(((Player) projectile.getShooter()));

                if (cache == null || cache.getMode() == GameJoinMode.SPECTATING) {
                    projectile.remove();

                    try {
                        if (projectile instanceof Arrow)
                            ((Arrow) projectile).setDamage(0);
                    } catch (final Throwable t) {
                        // Old MC
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHangingPlace(final HangingPlaceEvent event) {
        preventHangingGrief(event.getEntity(), event);
    }

    @EventHandler
    public void onHangingBreak(final HangingBreakEvent event) {
        preventHangingGrief(event.getEntity(), event);
    }

    private void preventHangingGrief(final Entity hanging, final Cancellable event) {
        final Game game = Game.findByLocation(hanging.getLocation());

        if (game != null && !game.getState().isEdited())
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        preventBuild(event.getPlayer(), event, false);
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        preventBuild(event.getPlayer(), event, true);
    }

    private <T extends BlockEvent & Cancellable> void preventBuild(final Player player, final T event, boolean place) {
        final Game game = Game.findByLocation(event.getBlock().getLocation());

        if (game != null) {
            final PlayerCache gamePlayer = game.getPlayerGetter().getPlayerInGame(player);
            final Block block = event.getBlock();

            if (gamePlayer == null) {
                Messenger.warn(player, "You cannot build unless you do '/game edit' first.");

                event.setCancelled(true);
                return;
            }

            if (gamePlayer.getMode() == GameJoinMode.EDITING)
                return;

            if (gamePlayer.getMode() == GameJoinMode.SPECTATING) {
                event.setCancelled(true);

                return;
            }

            try {
                if (place)
                    game.getGameEvents().onBlockPlace(player, block, (BlockPlaceEvent) event);
                else
                    game.getGameEvents().onBlockBreak(player, block, (BlockBreakEvent) event);

            } catch (final EventHandledException ex) {
                event.setCancelled(ex.isCancelled());
            }
        }
    }

    @EventHandler
    public void onItemDrop(final PlayerDropItemEvent event) {
        preventItemGrief(event, event.getItemDrop());
    }

    private <T extends PlayerEvent & Cancellable> void preventItemGrief(final T event, final Item item) {
        preventItemGrief(event.getPlayer(), event, item);
    }

    private <T extends PlayerEvent & Cancellable> void preventItemGrief(final Player player, final Cancellable event, final Item item) {
        final Game gameAtLocation = Game.findByLocation(item.getLocation());

        if (gameAtLocation == null)
            return;

        if (!gameAtLocation.getState().isEdited() && !gameAtLocation.getState().isPlayed()) {
            event.setCancelled(true);

            return;
        }

        final PlayerCache cache = gameAtLocation.getPlayerGetter().getPlayerInGame(player);

        if (cache == null || cache.getMode() == GameJoinMode.SPECTATING)
            event.setCancelled(true);
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

    private GameEvents getEvents(Player player) {
        Game game = PlayerCache.from(player).getGameIdentifier().getCurrentGame();

        if (game != null)
            return game.getGameEvents();

        return null;
    }

    private void cancelIfInStoppedOrLobby(Cancellable event, Entity entity) {
        this.cancelIfInStoppedOrLobby(event, entity.getLocation());
    }

    private void cancelIfInGame(Cancellable event, Location location) {
        Game game = Game.findByLocation(location);

        if (game != null)
            event.setCancelled(true);
    }

    private void cancelIfInStoppedOrLobby(Cancellable event, Location location) {
        Game game = Game.findByLocation(location);

        if (game != null && !game.getState().isPlayed() && !game.getState().isEdited())
            event.setCancelled(true);
    }


    private void executeIfPlayingGame(PlayerEvent event, BiConsumer<Player, PlayerCache> consumer) {
        Player player = event.getPlayer();
        PlayerCache cache = PlayerCache.from(player);

        if (cache.getGameIdentifier().hasGame() && cache.getMode() != GameJoinMode.EDITING)
            consumer.accept(player, cache);
    }

    /**
     * A separate listener for newer MC versions
     */
    private class EntityBreedListener implements Listener {

        /**
         * Prevent entities breeding in stopped non edited arenas
         *
         * @param event
         */
        @EventHandler
        public void onEntityBreed(final EntityBreedEvent event) {
            cancelIfInStoppedOrLobby(event, event.getEntity());
        }
    }

    /**
     * A separate listener for newer MC versions
     */
    private class SpawnerSpawnListener implements Listener {

        /**
         * Prevent spawners from functioning in stopped non edited arenas
         *
         * @param event
         */
        @EventHandler
        public void onSpawnerSpawn(final SpawnerSpawnEvent event) {
            cancelIfInStoppedOrLobby(event, event.getEntity());
        }
    }

    /**
     * A separate listener for newer MC versions
     */
    private class ExplodeAndEntitySpawnListener implements Listener {

        /**
         * Prevent any entity spawning in stopped arenas
         *
         * @param event
         */
        @EventHandler
        public void onEntitySpawn(final EntitySpawnEvent event) {

            final Entity entity = event.getEntity();
            final Game game = Game.findByLocation(event.getLocation());

            if (game == null)
                return;

            if (!game.getState().isPlayed() && !game.getState().isEdited()) {
                event.setCancelled(true);

                return;
            }

            if (ENTITY_TYPE_MANIPULATION_BLACKLIST.contains(entity.getType().toString()) && !game.getState().isEdited())
                event.setCancelled(true);
        }
    }

    /**
     * A separate listener for newer MC versions
     */
    private class ConsumeItemListener implements Listener {

        /**
         * Cancel food consumption in stopped arenas or during lobby
         *
         * @param event
         */
        @EventHandler
        public void onConsumeFood(final PlayerItemConsumeEvent event) {
            cancelIfInStoppedOrLobby(event, event.getPlayer());
        }
    }

    /**
     * A separate listener for newer MC versions
     */
    private class BlockReceiveGameListener implements Listener {

        /**
         * Cancels sculk sensor activating in arenas
         *
         * @param event
         */
        @EventHandler(priority = EventPriority.LOWEST)
        public void onSculkActivate(BlockReceiveGameEvent event) {
            cancelIfInGame(event, event.getBlock().getLocation());
        }
    }

    /**
     * A separate listener for newer MC versions
     */
    private class CauldronLevelChangeListener implements Listener {

        /**
         * Prevents cauldron from emptying in arenas
         *
         * @param event
         */
        @EventHandler
        public void onCauldronLevelChange(CauldronLevelChangeEvent event) {
            cancelIfInGame(event, event.getBlock().getLocation());
        }
    }

    /**
     * A separate listener for newer MC versions
     */
    private class EntityEnterBlockListener implements Listener {

        /**
         * Prevent bees entering a bee hive in stopped arenas
         *
         * @param event
         */
        @EventHandler
        public void onEntityEnterBlock(EntityEnterBlockEvent event) {
            cancelIfInStoppedOrLobby(event, event.getBlock().getLocation());
        }
    }

    /**
     * A separate listener for newer MC versions
     */
    private class EntityPickupItemListener implements Listener {

        /**
         * Prevent entities from picking up items in stopped arenas
         *
         * @param event
         */
        @EventHandler
        public void onEntityPickupItem(EntityPickupItemEvent event) {

            if (event.getEntity() instanceof Player)
                preventItemGrief((Player) event.getEntity(), event, event.getItem());

            else
                cancelIfInStoppedOrLobby(event, event.getItem());
        }
    }

    /**
     * A separate listener for older MC versions
     */
    private class PlayerPickupItemListener implements Listener {

        /**
         * Prevent item pickup in stopped arenas or by non playing players
         *
         * @param event
         */
        @EventHandler
        public void onItemPickup(final PlayerPickupItemEvent event) {
            preventItemGrief(event, event.getItem());
        }
    }

    /**
     * A separate listener for newer MC versions
     */
    private class PlayerBucketEntityListener implements Listener {

        /**
         * Prevent fishing in stopped arenas or during lobby
         *
         * @param event
         */
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerBucket(PlayerBucketEntityEvent event) {
            cancelIfInStoppedOrLobby(event, event.getEntity());
        }
    }

    /**
     * A separate listener for older MC versions
     */
    private class PlayerBucketFishListener implements Listener {

        /**
         * Prevent fishing in stopped arenas or during lobby
         *
         * @param event
         */
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerFish(PlayerBucketFishEvent event) {
            cancelIfInStoppedOrLobby(event, event.getEntity());
        }
    }

    /**
     * A separate listener for newer MC versions
     */
    private class PlayerTakeLecternBookListener implements Listener {

        /**
         * Completely prevent lectern interaction in arenas
         *
         * @param event
         */
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerTakeLectern(PlayerTakeLecternBookEvent event) {
            cancelIfInGame(event, event.getLectern().getLocation());
        }
    }

    /**
     * A separate listener for newer MC versions
     */
    private class RaidTriggerListener implements Listener {

        /**
         * Prevent raids from happening in case the arena is built onto a village
         *
         * @param event
         */
        @EventHandler(priority = EventPriority.LOWEST)
        public void onRaidTrigger(RaidTriggerEvent event) {
            cancelIfInGame(event, event.getRaid().getLocation());
        }
    }


}
