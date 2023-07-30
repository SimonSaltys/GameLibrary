package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.enums.State;
import dev.tablesalt.gamelib.game.map.GameMap;
import dev.tablesalt.gamelib.game.types.GameTypeList;
import dev.tablesalt.gamelib.game.types.Type;
import dev.tablesalt.gamelib.game.utils.GameFile;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.StrictList;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.settings.YamlConfig;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Consumer;

/**
 * Holds all the relevant information to run
 * a minecraft mini-game through the use of its
 * helper classes
 * <p>
 * When extending this class, you can override the
 * protected methods to provide specific functionality
 * to your game.
 *
 * @author OnlyTableSalt
 */
public abstract class Game extends YamlConfig {
    @Getter
    private Type<Game> type;
    @Getter
    protected final GameHeartbeat heartbeat;
    @Getter
    protected final GameScoreboard scoreboard;

    /*----------------------------------------------------------------*/
    /* Helper Classes */
    /*----------------------------------------------------------------*/
    @Getter
    private final PlayerJoiner playerJoiner;
    @Getter
    private final PlayerLeaver playerLeaver;
    @Getter
    private final PlayerGetter playerGetter;
    @Getter
    private final Starter starter;
    @Getter
    private final Stopper stopper;
    @Getter
    private final GameBroadcaster gameBroadcaster;
    @Getter
    private final MapRotator mapRotator;
    @Getter
    private final MapLoader mapLoader;
    @Getter
    private final State state;

    /*----------------------------------------------------------------*/
    /* Config Items */
    /*----------------------------------------------------------------*/
    @Getter
    private int minPlayers;
    @Getter
    private int maxPlayers;
    @Getter
    private SimpleTime lobbyDuration;
    public abstract Type<GameMap> getGameMapType();

    public abstract Plugin getOwningPlugin();


    /*----------------------------------------------------------------*/
    /* Volatile Variables */
    /*----------------------------------------------------------------*/
    @Getter
    private final StrictList<PlayerCache> playersInGame = new StrictList<PlayerCache>();

    protected Game(String name, Type<Game> type) {
        this.setHeader(
                Common.configLine(),
                "This file stores information about a single game.",
                Common.configLine() + "\n"
        );

        //Initialize Helpers that can be overrided, Order matters here
        this.type = type;
        //It is important that the type is set before calling load configuration. If we do not we will get a null pointer
        loadConfiguration(NO_DEFAULT, GameFile.getGamesTypeFolder(type).getPath() + "/" + name + ".yml");

        //Initialize Helpers Order matters here
        scoreboard = compileScoreboard();
        heartbeat = compileHeartbeat();
        playerJoiner = compileJoiner();
        playerLeaver = compileLeaver();
        starter = compileStarter();
        stopper = compileStopper();
        mapLoader = new MapLoader(this);
        playerGetter = new PlayerGetter(this);
        gameBroadcaster = new GameBroadcaster(this);
        mapRotator = compileMapRotator();
        state = new State();
        save();
    }

    protected GameHeartbeat compileHeartbeat() {
        return new GameHeartbeat(this);
    }

    protected GameScoreboard compileScoreboard() {
        return new GameScoreboard(this);
    }

    protected Stopper compileStopper() {return new Stopper(this);}

    protected Starter compileStarter() {return new Starter(this); }

    protected MapRotator compileMapRotator() {return new MapRotator(this);}

    protected PlayerJoiner compileJoiner() {return new PlayerJoiner(this);}

    protected PlayerLeaver compileLeaver() {return new PlayerLeaver(this);}

    @Override
    protected void onLoad() {
        try {
            if (type == null)
                type = GameTypeList.getInstance().getType(getString("Type"));

        } catch (Throwable t) {
            Common.error(t,"Could not load game type for Game " + getName());
        }

        minPlayers = getInteger("Min_Players", 2);
        maxPlayers = getInteger("Max_Players", 2);
        lobbyDuration = getTime("Lobby_Duration", SimpleTime.from("10 seconds"));
        save();
    }

    @Override
    protected void onSave() {
        set("Type", type.getName());
        set("Min_Players", minPlayers);
        set("Max_Players", maxPlayers);
        set("Lobby_Duration", lobbyDuration);
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        save();
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        save();
    }

    /*----------------------------------------------------------------*/
    /* Utility Methods */
    /*----------------------------------------------------------------*/

    protected void clearPlayers() {
        playersInGame.clear();
    }

    protected void removePlayer(PlayerCache cache) {
        playersInGame.remove(cache);
    }

    protected void addPlayer(PlayerCache cache) {
        playersInGame.add(cache);
    }


    /*----------------------------------------------------------------*/
    /* STATIC */
    /*----------------------------------------------------------------*/



    private static final Map<Type<Game>,List<Game>> LOADED_GAME_FILES_MAP = new HashMap<>();

    public static void loadGamesOfType(Type<Game> type) {

        List<Game> gamesOfType = new ArrayList<>();

        File gameTypeFolder = GameFile.getGamesTypeFolder(type);

        for (File file : GameFile.getFiles(gameTypeFolder.getPath(),"yml")) {
            final YamlConfig config = YamlConfig.fromFileFast(file);

            String gameName = file.getName().replace(".yml",""); //trims the name down without .yml
            Valid.checkBoolean(!isGameLoaded(gameName), "Game " + gameName + " is already loaded");

            String typeName = config.getString("Type");
            Type<Game> fileGameType = GameTypeList.getInstance().getType(typeName);

            Valid.checkNotNull(type, "Unrecognized Game Type." + config.getObject("Type") + " in " + gameName +
                    "! Available: " + Common.join(GameTypeList.getInstance().getTypeNames()));

            Valid.checkBoolean(fileGameType.equals(type),"Types do not match FILE TYPE:" + gameName
                    + " TYPE NEEDED: " + type.getName());

            Game game = Game.instantiate(gameName,type);
            gamesOfType.add(game);
            Common.broadcast("LOADED GAME: &e" + game.getName() + "&r OF TYPE: &e" + game.getType().getName());

        }

        LOADED_GAME_FILES_MAP.putIfAbsent(type,gamesOfType);

    }

    /**
     * Attempts to create a game of provided name and type
     * @param name the name of the game
     * @param type the type of the game
     * @return the created game
     */
    public static Game createGame(@NonNull final String name, @NonNull final Type<Game> type) {
        Valid.checkBoolean(!isGameLoaded(name), "Game " + name + " is already loaded");
        Game game = Game.instantiate(name,type);
        LOADED_GAME_FILES_MAP.get(type).add(game);

        return game;
    }

    private static <T extends Game> T instantiate(String name, Type<Game> type) {
        final Constructor<?> constructor = ReflectionUtil.getConstructor(type.getInstanceClass(), String.class);
        Valid.checkNotNull(constructor, "Unable to find constructor for game class " + name + " ensure you " +
                "have a public constructor taking in the game name!");
        return (T) ReflectionUtil.instantiate(constructor, name);
    }

    /**
     * Attempts to remove the game provided
     */
    public static void removeGame(final Game game) {
        List<Game> foundList = null;

      for (List<Game> gameList : LOADED_GAME_FILES_MAP.values()) {
          for (Game searchedGame : gameList)
              if (searchedGame.equals(game))
                  foundList = gameList;
      }

      if (foundList != null) {
          MapLoader loader = game.getMapLoader();
          for (GameMap map : loader.getMaps())
              loader.deleteMap(map);


          foundList.remove(game);
          game.deleteFile();
      }


    }

    public static Game getGame(String gameName) {
        for (Game game : getGames())
            if (game.getName().equals(gameName))
                return game;
        return null;
    }

    public static List<Game> getGames() {
        List<Game> foundGames = new ArrayList<>();

       forEachGame(foundGames::add);

        return foundGames;
    }

    public static Game findByName(@NonNull final String gameName) {

        return getGame(gameName);
    }

    public static Game findByLocation(final Location location) {
        for (final Game game : getGames()) {
            GameMap map = game.getMapRotator().getCurrentMap();
            if(map != null && map.getRegion().isWhole() && map.getRegion().isWithin(location))
                return game;
        }

        return null;
    }

    public static List<Game> getGamesOfType(Type<Game> type) {
        List<Game> games = new ArrayList<>();

        for (Game game : getGames())
            if (game.getType().equals(type))
                games.add(game);

        return games;
    }


    public static List<String> getGameNames() {
        return Common.convert(getGames(),Game::getName);
    }

    /**
     * @param gameName the name of the game
     * @return if the game is loaded
     */
    public static boolean isGameLoaded(final String gameName) {
      for (List<Game> games : LOADED_GAME_FILES_MAP.values())
          for (Game game : games)
              if (game.getName().equals(gameName))
                  return true;
        return false;
    }

    private static void forEachGame(Consumer<Game> consumer) {
        for (List<Game> games : LOADED_GAME_FILES_MAP.values())
            for (Game game : games)
                consumer.accept(game);
    }
}
