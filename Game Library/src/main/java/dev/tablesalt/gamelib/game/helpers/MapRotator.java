package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.map.GameMap;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.exception.FoException;

import java.util.List;


public class MapRotator {

    private final Game game;

    private GameMap currentMap;


   protected MapRotator(Game game) {
        this.game = game;
        List<? extends GameMap> maps = game.getMapLoader().getMaps();

        if (!maps.isEmpty())
           currentMap = RandomUtil.nextItem(maps);
    }


    public GameMap getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(GameMap map) {
            currentMap = map;
    }




}
