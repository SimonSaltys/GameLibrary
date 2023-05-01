package dev.tablesalt.gamelib.game.types;

import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.map.GameMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.command.Command;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.Valid;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameTypeList {

    private final List<Type<Game>> gameTypes = new ArrayList<>();

    @Getter
    private static final GameTypeList instance = new GameTypeList();


    public void addType(Type<Game> type) {
        gameTypes.add(type);
        Game.loadGamesOfType(type);
    }


    public List<Type<Game>> getTypes() {
        return Collections.unmodifiableList(gameTypes);
    }

    public List<String> getTypeNames() { return Common.convert(gameTypes, Type::getName); }

    public Type<Game> getType(String name) {
        for (Type<Game> gameType : gameTypes)
            if (gameType.getName().equals(name))
                return gameType;

        Common.error(new Throwable(), "could not find game type by name of " + name);

        return null;
    }
}

