package dev.tablesalt.gamelib.game.types;

import lombok.Getter;

@Getter
public class Type<T> {

    private final String name;
    private final Class<? extends T> instanceClass;

    public Type(String name, Class<? extends T> instanceClass) {
        this.name = name;
        this.instanceClass = instanceClass;
    }
}
