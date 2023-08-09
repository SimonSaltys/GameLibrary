package dev.tablesalt.gamelib.event;

import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.utils.Message;
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
    @Getter
    private Message leaveMessage;

    public PlayerLeaveGameEvent(Player player, Game game, Message leaveMessage) {
        this.player = player;
        this.leftGame = game;
        if (leaveMessage == null)
            this.leaveMessage = Message.NO_MESSAGE;
        else
            this.leaveMessage = leaveMessage;
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
