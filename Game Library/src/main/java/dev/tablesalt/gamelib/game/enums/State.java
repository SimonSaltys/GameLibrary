package dev.tablesalt.gamelib.game.enums;

import dev.tablesalt.gamelib.game.enums.GameState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class State {

    @Setter
    private GameState state = GameState.STOPPED;

    public boolean isLobby() { return state == GameState.LOBBY; }
    public boolean isPlayed() {
        return state == GameState.PLAYED;
    }

    public boolean isEdited() {
        return state == GameState.EDITED;
    }

    public boolean isStopped() {
        return state == GameState.STOPPED;
    }

    @Override
    public String toString() {
        return state.getLocalized();
    }
}
