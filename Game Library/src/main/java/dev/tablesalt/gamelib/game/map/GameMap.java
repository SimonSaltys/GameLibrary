package dev.tablesalt.gamelib.game.map;

import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.types.Type;
import dev.tablesalt.gamelib.game.utils.GameFile;
import lombok.Getter;
import org.bukkit.Location;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;
import org.mineacademy.fo.visual.VisualizedRegion;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GameMap extends YamlConfig {

    @Getter
    private VisualizedRegion region;
    @Getter
    private VisualizedRegion lobbyRegion;

    protected final Game game;

    protected GameMap(String name, Game game) {
        this.setHeader(
                Common.configLine(),
                "This file stores information about a single map.",
                Common.configLine() + "\n"
        );

        this.game = game;
        this.loadConfiguration(NO_DEFAULT, GameFile.getGamesMapFolder(game).getPath() + "/" + name + ".yml");
        save();
    }

    @Override
    protected void onLoad() {
        this.region = get("Region", VisualizedRegion.class, new VisualizedRegion());
        this.lobbyRegion = get("Lobby_Region",VisualizedRegion.class, new VisualizedRegion());
        this.save();
    }

    /**
     * @see org.mineacademy.fo.settings.YamlConfig#serialize()
     */
    @Override
    protected void onSave() {
        this.set("Region", this.region);
        this.set("Lobby_Region", this.lobbyRegion);
    }

    public boolean isSetup() {
        return (lobbyRegion != null && lobbyRegion.isWhole()) && (region != null && region.isWhole());
    }

    public final void setLobbyLocation(VisualizedRegion lobbyRegion) {
        this.lobbyRegion = lobbyRegion;
        save();
    }

    public final void setRegion(VisualizedRegion region) {
        this.region = region;
        save();
    }

    public Game getGame() {
        return game;
    }

    public static <T extends GameMap> T instantiate(String name, Game game) {
        final Constructor<?> constructor = ReflectionUtil.getConstructor(game.getGameMapType().getInstanceClass(), String.class, Game.class);
        Valid.checkNotNull(constructor, "Unable to find constructor for game map class " + name + " ensure you " +
                "have a public constructor taking in the map name and game!");

        Common.broadcast("&aInstantiating map: " + name + " for game: " + game.getName());
        return (T) ReflectionUtil.instantiate(constructor, name, game);
    }

    public static GameMap findMapFromLocation(Location location) {
        for (Game checkGame : Game.getGames())
            for (GameMap map : checkGame.getMapLoader().getMaps())
                if (map.getRegion().isWithin(location))
                    return map;
        return null;
    }
}
