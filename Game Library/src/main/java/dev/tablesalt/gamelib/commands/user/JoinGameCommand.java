package dev.tablesalt.gamelib.commands.user;

import dev.tablesalt.gamelib.commands.GameSubCommand;
import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.helpers.Game;

import java.util.List;

public final class JoinGameCommand extends GameSubCommand {

    private JoinGameCommand() {
        super("join", 1, "<name>", "joins the specified game");
    }


    @Override
    protected void onCommand() {
        checkConsole();

        String gameName = args[0];
        checkNotInGame();
        checkGameLoaded(gameName);

        Game gameToJoin = findGame(gameName);
        gameToJoin.getPlayerJoiner().joinPlayer(getPlayer(), GameJoinMode.PLAYING);
    }


    @Override
    protected List<String> tabComplete() {
        if (args.length == 1)
            return Game.getGameNames();

        return NO_COMPLETE;
    }
}
