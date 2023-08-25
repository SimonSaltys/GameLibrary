package dev.tablesalt.gamelib.event;

import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.utils.Message;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinGameEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    @Getter
    private Game joinedGame;
    @Getter
    private Player player;
    @Getter
    private GameJoinMode mode;

    public PlayerJoinGameEvent(Player player, Game game, GameJoinMode mode) {
        this.player = player;
        this.joinedGame = game;
        this.mode = mode;

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
