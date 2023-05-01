package dev.tablesalt.gamelib.commands.admin;

import dev.tablesalt.gamelib.commands.GameSubCommand;
import dev.tablesalt.gamelib.game.helpers.Game;
import org.mineacademy.fo.Common;

import java.util.List;

public final class TESTCOMMAND extends GameSubCommand {


    private TESTCOMMAND() {
        super("something", 0, "", "REMOVE THIS COMMAND LATER, FOR DEVELOPMENT USE ONLY");
        setAsAdmin();
    }
    @Override
    protected void onCommand() {
        for (Game game : Game.getGames())
            Common.broadcast(game.getState() + "");
    }

}
