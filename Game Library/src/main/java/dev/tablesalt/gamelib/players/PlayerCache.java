package dev.tablesalt.gamelib.players;

import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.players.helpers.GameIdentifier;
import dev.tablesalt.gamelib.players.helpers.PlayerStateIdentifier;
import dev.tablesalt.gamelib.players.helpers.PlayerTagger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.remain.Remain;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
@Getter
public class PlayerCache {

    private static final Map<UUID, PlayerCache> cacheMap = new HashMap<>();

    private final UUID uniqueId;

    private final String playerName;

    private final PlayerTagger tagger;

    private final PlayerStateIdentifier stateIdentifier;
    private final GameIdentifier gameIdentifier;

    @Setter
    private String currentGameName;
    @Getter
    private GameJoinMode mode;

    private PlayerCache(String name, UUID uniqueId) {
        this.playerName = name;
        this.uniqueId = uniqueId;

        //helper classes
        this.tagger = new PlayerTagger();
        this.stateIdentifier = new PlayerStateIdentifier();
        this.gameIdentifier = new GameIdentifier(this);
    }

    public void setGameJoinMode(GameJoinMode mode) {
        this.mode = mode;
    }

    public void reset() {
        stateIdentifier.setPlayerState(PlayerStateIdentifier.PlayerState.NONE);
        tagger.clearTags();
        currentGameName = null;
        mode = null;
    }

    public void save() {
        //todo save to db
    }

    public void load() {
        //todo load from db
    }

    public void removeFromMemory() {
        synchronized (cacheMap) {
            cacheMap.remove(this.uniqueId);
        }
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerCache && ((PlayerCache) obj).getUniqueId().equals(this.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uniqueId);
    }

    @Override
    public String toString() {
        return "PlayerCache{" + this.playerName + ", " + this.uniqueId + "}";
    }

    @Nullable
    public Player toPlayer() {
        final Player player = Remain.getPlayerByUUID(this.uniqueId);

        return player != null && player.isOnline() ? player : null;
    }

    /* ------------------------------------------------------------------------------- */
    /* Static access */
    /* ------------------------------------------------------------------------------- */

    /**
     * Return or create new player cache for the given player
     *
     * @param player
     * @return
     */
    public static PlayerCache from(Player player) {
        synchronized (cacheMap) {
            final UUID uniqueId = player.getUniqueId();
            final String playerName = player.getName();

            PlayerCache cache = cacheMap.get(uniqueId);

            if (cache == null) {
                cache = new PlayerCache(playerName, uniqueId);

                cacheMap.put(uniqueId, cache);
            }

            return cache;
        }
    }


}
