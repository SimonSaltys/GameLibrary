package dev.tablesalt.gamelib.game.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface Message {

    static Message NO_MESSAGE = null;


     Component getMessage();




}
