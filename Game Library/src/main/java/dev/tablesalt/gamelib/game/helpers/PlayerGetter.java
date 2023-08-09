package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class PlayerGetter {
    private final Game game;

    public final void forEachInAllModes(final Consumer<PlayerCache> consumer) {
        forEach(consumer, null);
    }

    public final void forEach(final Consumer<PlayerCache> consumer, GameJoinMode mode) {
        for (final PlayerCache cache : this.getPlayers(mode))
            consumer.accept(cache);
    }

    public final List<PlayerCache> getPlayers(@Nullable final GameJoinMode mode) {
        final List<PlayerCache> foundPlayers = new ArrayList<>();

        for (final PlayerCache otherCache : game.getPlayersInGame())
            if (mode == null || (otherCache.getGameIdentifier().hasGame()) && otherCache.getMode() == mode)
                foundPlayers.add(otherCache);

        return Collections.unmodifiableList(foundPlayers);
    }

    public PlayerCache getPlayerInGame(Player player) {
        GameUtil.checkIntegrity(game);

            for (final PlayerCache otherCache : getPlayersInAllModes())
                if (otherCache.getGameIdentifier().hasGame() && otherCache.getGameIdentifier().currentGameEquals(game)
                        && otherCache.getUniqueId().equals(player.getUniqueId()))

                    return otherCache;
            return null;
    }

    public final List<Player> getBukkitPlayersInAllModes() {
        return Common.convert(getPlayers(null), PlayerCache::toPlayer);
    }

    public final List<Player> getBukkitPlayers(GameJoinMode mode) {
       return Common.convert(getPlayers(mode), PlayerCache::toPlayer);
    }

    public final List<PlayerCache> getPlayersInAllModes() {
        return Collections.unmodifiableList(game.getPlayersInGame().getSource());
    }





    
    
    
    
    
    
    
}
