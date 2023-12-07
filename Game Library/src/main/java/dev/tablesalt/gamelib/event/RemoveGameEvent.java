package dev.tablesalt.gamelib.event;

import dev.tablesalt.gamelib.game.helpers.Game;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RemoveGameEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    @Getter
    private Game game;

    public RemoveGameEvent(Game game) {
        this.game = game;
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
