package dev.tablesalt.gamelib.commands.admin;

import dev.tablesalt.gamelib.commands.GameSubCommand;
import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.players.PlayerCache;
import dev.tablesalt.gamelib.tools.GameTool;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.menu.MenuTools;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public final class GameToolsCommand extends GameSubCommand {
    private GameToolsCommand() {
        super("tools", 0, "", "Get the tools to edit the current game you are in");

    }

    @Override
    protected void onCommand() {
        checkConsole();

        PlayerCache cache = PlayerCache.from(getPlayer());
        checkBoolean(cache.getMode() == GameJoinMode.EDITING, "You may only use this command while editing a game.");
        checkInGame();

        Common.tellNoPrefix(cache.toPlayer(),"One Moment gathering tools...");

        //We are doing some heavy reflection, so running async to not bog down the main thread
        Common.runAsync(() -> {
            new MenuTools() {
                @Override
                protected Object[] compileTools() {
                    return getRelevantTools(cache.getGameIdentifier().getCurrentGame()).toArray();
                }
            }.displayTo(cache.toPlayer());
        });
    }
    public List<Object> getRelevantTools(Game game) {
        List<Object> relevantInstances = new ArrayList<>();

        for (Class<GameTool> tool : getToolClassesReflectively(game)) {
            if (Modifier.isAbstract(tool.getModifiers()))
                continue;

            GameTool instance = ReflectionUtil.getFieldContent(tool, "instance", null);

            if (instance.isApplicable(game))
                relevantInstances.add(instance);
        }
        return relevantInstances;
    }

    private Collection<Class<GameTool>> getToolClassesReflectively(Game game) {
        TreeSet<Class<GameTool>> tools = ReflectionUtil.getClasses(game.getOwningPlugin(),GameTool.class);
        tools.addAll(ReflectionUtil.getClasses(SimplePlugin.getInstance(),GameTool.class));

        return tools;
    }
}
