package dev.tablesalt.gamelib.game.utils;

import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.remain.Remain;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Consumer;

@UtilityClass
public class MessageUtil {

    public String makeInfo(String message) {
        return "&e&lINFO!&r " + message;
    }

    public String makeError(String message) {

        return "&c&lERROR!&r " + message;

    }

    public String makeSuccessful(String message) {
        return "&a&bSUCCESS!&r " + message;
    }

    public String makeScary(String message) {
        return "&c&lOH NO! " + message;
    }


    public String getPromptPrefix() {
        return "&c&lPROMPT! &r";
    }

    public void clearTitle(Player player) {
        Remain.sendTitle(player,"","");
    }

    public void forAllPlayersNotInGame(Consumer<Player> consumer) {
        for (Player player : Remain.getOnlinePlayers())
            if (!PlayerCache.from(player).getGameIdentifier().hasGame())
                consumer.accept(player);
    }
}
