package dev.tablesalt.gamelib.commands.admin;

import dev.tablesalt.gamelib.commands.GameSubCommand;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.map.GameMap;
import org.mineacademy.fo.Common;

import java.util.List;

public final class CreateMapCommand extends GameSubCommand {

    private CreateMapCommand() {
        super("map", 2, "<name of game> <name of map>", "Creates a map for the specified game");

    }

    @Override
    protected void onCommand() {
        checkNotInGame();
        String gameName = args[0];
        String mapName = args[1];

        checkGameLoaded(gameName);

        Game game = findGame(gameName);
        checkGameActive(game);
        checkBoolean(!game.getMapLoader().isMapLoaded(mapName), Common.format("Map %s already exists. Pick a different name", mapName));


        game.getMapLoader().createMap(mapName);
        tellSuccess("Created " + mapName + " map for " + gameName + "!");
    }

    @Override
    protected List<String> tabComplete() {
        if (args.length == 1)
            return Game.getGameNames();

        return NO_COMPLETE;
    }
}
