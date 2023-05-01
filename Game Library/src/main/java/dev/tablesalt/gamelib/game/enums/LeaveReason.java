package dev.tablesalt.gamelib.game.enums;

import dev.tablesalt.gamelib.game.utils.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Replacer;

@RequiredArgsConstructor
public enum LeaveReason implements Message {

    DISCONNECT("asdasd");
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getMessageWithReplacements(Player player, Object... replacements) {
        return Replacer.replaceArray(getMessage(),replacements);
    }
}
