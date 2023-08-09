package dev.tablesalt.gamelib.game.enums;

import dev.tablesalt.gamelib.game.utils.Message;
import dev.tablesalt.gamelib.game.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Replacer;

@RequiredArgsConstructor
public enum LeaveReason implements Message {

    DISCONNECT("Player Disconnected");
    private final String message;



    @Override
    public String getMessage() {
        return message;
    }


}
