package dev.tablesalt.gamelib.game.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GameJoinMode {
        PLAYING("playing"),
        EDITING("editing"),
        SPECTATING("spectating");
        @Getter
        private final String localized;



}