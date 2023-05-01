package dev.tablesalt.gamelib.game.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GameState {

    STOPPED("stopped"),

    LOBBY("lobby"),

    PLAYED("played"),

    EDITED("edited");

    @Getter
    private final String localized;
}
