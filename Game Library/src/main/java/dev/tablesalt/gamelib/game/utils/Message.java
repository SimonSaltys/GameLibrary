package dev.tablesalt.gamelib.game.utils;

import org.bukkit.entity.Player;

public interface Message {

    static Message NO_MESSAGE = null;


     String getMessage();

     String getMessageWithReplacements(Player player, Object... replacements);


}
