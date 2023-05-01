package dev.tablesalt.gamelib.commands.admin;

import dev.tablesalt.gamelib.commands.GameSubCommand;
import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.helpers.Game;

import java.util.List;

public final class EditGameCommand extends GameSubCommand {

    private EditGameCommand() {
        super("edit", 1, "<name>", "Enters edit mode for the specified game");
        setAsAdmin();
    }
    @Override
    protected void onCommand() {
        checkConsole();
        checkNotInGame();

        String gameName = args[0];

        checkGameLoaded(gameName);

        Game game = Game.getGame(gameName);
        checkGameActive(game);

        game.getPlayerJoiner().joinPlayer(getPlayer(), GameJoinMode.EDITING);
    }

    @Override
    protected List<String> tabComplete() {
        if (args.length == 1)
            return Game.getGameNames();
        return NO_COMPLETE;
    }
}
