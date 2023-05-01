package dev.tablesalt.gamelib.commands.admin;

import dev.tablesalt.gamelib.commands.GameSubCommand;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.types.GameTypeList;
import dev.tablesalt.gamelib.game.types.Type;

import java.util.List;

public final class CreateGameCommand extends GameSubCommand {
    private CreateGameCommand() {
        super("create", 2, "<type> <name>", "creates a new game of specified type");
        setAsAdmin();
    }

    @Override
    protected void onCommand() {
        final Type<Game> type = this.findType(args[0]);
        final String name = this.joinArgs(1);

        checkGameNotLoaded(name);
        Game.createGame(name,type);
        tellSuccess("Created " + type.getName() + " game '" + name + "'!");
    }
    @Override
    protected List<String> tabComplete() {
        return this.args.length == 1 ? this.completeLastWord(GameTypeList.getInstance().getTypeNames()) : NO_COMPLETE;
    }

}
