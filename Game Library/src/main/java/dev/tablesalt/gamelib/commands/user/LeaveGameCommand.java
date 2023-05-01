package dev.tablesalt.gamelib.commands.user;

import dev.tablesalt.gamelib.commands.GameSubCommand;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.utils.Message;
import dev.tablesalt.gamelib.players.PlayerCache;
import org.bukkit.entity.Player;

public final class LeaveGameCommand extends GameSubCommand {

    private LeaveGameCommand() {
        super("leave", 0, "", "leaves the current game you are in");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        checkInGame();
        Player player = getPlayer();
        PlayerCache cache = PlayerCache.from(player);

        Game game = cache.getGameIdentifier().getCurrentGame();

        game.getPlayerLeaver().leavePlayer(player, Message.NO_MESSAGE);
        cache.save();
    }
}
