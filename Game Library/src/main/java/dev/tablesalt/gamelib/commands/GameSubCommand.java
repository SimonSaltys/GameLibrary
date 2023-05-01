package dev.tablesalt.gamelib.commands;


import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.types.GameTypeList;
import dev.tablesalt.gamelib.game.types.Type;
import dev.tablesalt.gamelib.players.PlayerCache;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public abstract class GameSubCommand extends SimpleSubCommand {

    private static String ADMIN_LABEL = "game.admin";
    protected GameSubCommand(String sublabel, String description) {
        this(sublabel, 0, "", description);
    }

    protected GameSubCommand(String sublabel, int minArguments, String usage, String description) {
        super(sublabel);
        setMinArguments(minArguments);
        setUsage(usage);
        setDescription(description);
    }

    protected final void setAsAdmin() {
        setPermission(ADMIN_LABEL + "." + getSublabel());
    }

    protected final Game findGame(String name) {
        final Game arena = Game.findByName(name);
        checkNotNull(arena, Common.format("Game %s does not exist. Available: %s", name, Game.getGameNames()));

        return arena;
    }

    protected final void checkGameActive(Game game) {
        checkBoolean(game.getState().isStopped(),Common.format("Game %s needs to be stopped to run this command",game.getName()));
    }

    protected final Type<Game> findType(String name) {
        Type<Game> type = null;
        for (Type<Game> otherType : GameTypeList.getInstance().getTypes())
            if (otherType.getName().equals(name))
                type = otherType;

        checkNotNull(type, Common.format("No such game type '%s'. Available: %s", name, GameTypeList.getInstance().getTypeNames()));

        return type;
    }

    protected final Game findGameFromLocationOrFirstArg() {

        // TODO: 3/31/2023  
//        Game game;
//
//        if (this.args.length > 0)
//            game = this.findGame(this.joinArgs(0));
//        else
//            game = GameFinder.findByLocation(getPlayer().getLocation());
//
//        this.checkNotNull(game, "Unable to locate a game. Type a game name. Available " + Common.join(GameFinder.getGameNames()));
//
//        return game;
        return null;
    }

    protected final boolean checkInGame() {
        checkConsole();

        PlayerCache cache = PlayerCache.from(getPlayer());
        checkBoolean(cache.getGameIdentifier().hasGame(), "You are not joined in any game.");

        return cache.getGameIdentifier().hasGame();
    }

    protected final void checkNotInGame() {
        if (sender instanceof ConsoleCommandSender)
            return;

        PlayerCache cache = PlayerCache.from(getPlayer());
       if (cache.getGameIdentifier().hasGame())
            returnTell("You cannot perform this while " + cache.getMode().getLocalized()
                    + " game " + cache.getGameIdentifier().getCurrentGame().getName() + ".");
    }

    protected final void checkGameNotLoaded(String name) {
        checkBoolean(!Game.isGameLoaded(name), Common.format("Game %s already exists. Pick a different name", name));
    }

    protected final void checkGameLoaded(String name) {
        checkBoolean(Game.isGameLoaded(name), Common.format("Game %s does not exists. Available %s", name, Game.getGameNames()));
    }

    @Override
    protected List<String> tabComplete() {
        return NO_COMPLETE;
    }
}
