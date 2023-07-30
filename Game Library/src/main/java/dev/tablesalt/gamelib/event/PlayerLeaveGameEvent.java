package dev.tablesalt.gamelib.event;

import dev.tablesalt.gamelib.game.helpers.Game;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerLeaveGameEvent extends Event{
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    @Getter
    private Game leftGame;
    @Getter
    private Player player;

    public PlayerLeaveGameEvent(Player player, Game game) {
        this.player = player;
        this.leftGame = game;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
