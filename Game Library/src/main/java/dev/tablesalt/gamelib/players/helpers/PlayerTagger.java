package dev.tablesalt.gamelib.players.helpers;

import lombok.RequiredArgsConstructor;
import org.mineacademy.fo.collection.StrictMap;

@RequiredArgsConstructor
public class PlayerTagger {

    protected final StrictMap<String, Object> tagsForPlayer = new StrictMap<>();


    public <T> T getPlayerTag(final String key) {
        final Object value = tagsForPlayer.get(key);

        return value != null ? (T) value : null;
    }

    public boolean hasPlayerTag(final String key) {
        return getPlayerTag(key) != null;
    }

    public boolean getBooleanTagSafe(final String key) {
        Boolean bool = getPlayerTag(key);
        if (bool != null)
            return bool;
        return false;
    }

    public void setPlayerTag(final String key, final Object value) {
        tagsForPlayer.override(key, value);
    }

    public void removePlayerTag(final String key) {
        if (tagsForPlayer.containsKey(key))
            tagsForPlayer.remove(key);
    }

    public void clearTags() {
        tagsForPlayer.clear();
    }
}
