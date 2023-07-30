package dev.tablesalt.gamelib.game.utils;

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

@UtilityClass
public class MessageUtil {
    @Getter
    public final MiniMessage miniMessage = MiniMessage.miniMessage();

    public Component makeInfo(String message, @Nullable TagResolver... resolvers) {
        return makeMini("<yellow><bold>INFO!<reset> " + message, resolvers);
    }

    public Component makeError(String message, @Nullable TagResolver... resolvers) {

        return makeMini("<red><bold>ERROR!<reset> " + message, resolvers);

    }

    public Component makeSuccessful(String message, @Nullable TagResolver... resolvers) {
        return  makeMini("<green><bold>SUCCESS!<reset> " + message, resolvers);

    }

    public Component makeMini(String message, TagResolver... resolvers) {

        if (resolvers != null) {
            return miniMessage.deserialize(message,resolvers);
        }



        return miniMessage.deserialize(message);

    }

    public Component makeMini(String message) {
        return miniMessage.deserialize(message);
    }

    public void sendToConsole(Component component) {
        Bukkit.getConsoleSender().sendMessage(component);
    }

    public String getPromptPrefix() {
        return "&c&lPROMPT! &r";
    }

    public void clearTitle(Player player) {
        Remain.sendTitle(player,"","");
    }
}
