package dev.tablesalt.gamelib.game.helpers;

import com.massivecraft.massivecore.store.Coll;
import dev.tablesalt.gamelib.game.map.GameMap;
import dev.tablesalt.gamelib.game.utils.GameFile;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.settings.ConfigItems;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapLoader {

    private final Game game;

    private final List<GameMap> mapsForGame;

    MapLoader(Game game) {
        this.game = game;

        mapsForGame = new ArrayList<>();
        mapsForGame.addAll(loadMaps());
    }


    public List<GameMap> loadMaps() {
        List<GameMap> maps = new ArrayList<>();
        File gameMapFolder = GameFile.getGamesMapFolder(game);

       for (File file : FileUtil.getFiles(gameMapFolder.getPath(),"yml")) {
           //don't want the map name to have .yml in it
           String mapName = file.getName().replace(".yml","");

           if (isMapLoaded(mapName))
               continue;

           //Let's load the map using reflection
           GameMap map = GameMap.instantiate(mapName,game);
           maps.add(map);
           Common.broadcast("LOADED MAP: &a" + map.getName() + " &rFOR GAME: &e" + game.getName());
       }
       return maps;
    }


    public void createMap(String name) {
        Valid.checkBoolean(!isMapLoaded(name), "Map " + name + " is already loaded!");
        GameMap map = GameMap.instantiate(name,game);
        mapsForGame.add(map);
    }

    public List<GameMap> getMaps() {
        return Collections.unmodifiableList(mapsForGame);
    }

    public boolean isMapLoaded(final String mapName) {
       for (GameMap map : mapsForGame)
           if (map.getName().equals(mapName))
               return true;

       return false;
    }


}
