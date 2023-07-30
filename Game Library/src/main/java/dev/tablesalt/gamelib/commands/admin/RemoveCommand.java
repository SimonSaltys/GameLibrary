package dev.tablesalt.gamelib.commands.admin;

import dev.tablesalt.gamelib.commands.GameSubCommand;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.map.GameMap;
import org.mineacademy.fo.Common;

import java.util.Collections;
import java.util.Iterator;

public final class RemoveCommand extends GameSubCommand {
    private RemoveCommand() {
        super("remove", 1, "<name>", "removes a game or map of the provided name");
    }

    @Override
    protected void onCommand() {
        final String name = this.joinArgs(0);

        Game game = Game.findByName(name);

        if (game != null)
         checkGameActive(game);

        if (game != null) {
            Game.removeGame(game);
            tellSuccess("Successfully deleted game " + game + " and its respective maps");
       }

        if (game == null) {
            removeMap(name);
            tellSuccess("Successfully deleted map " + name + " configuration");

        }
    }

    private void removeMap(String mapName) {
        for (Game searchedGame : Game.getGames()) {
            Iterator<GameMap> itr = searchedGame.getMapLoader().getMaps().listIterator();

            while (itr.hasNext()) {
                GameMap searchedMap = itr.next();

                if (searchedMap.getName().equals(mapName))
                    searchedGame.getMapLoader().deleteMap(searchedMap);
            }
        }
    }
}
