package dev.tablesalt.gamelib.players.helpers;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerStateIdentifier {

    public enum PlayerState {
        ALIVE, DEAD, NONE
    }

   PlayerState playerState;
    public final boolean isPlayerAlive() {
        return playerState == PlayerState.ALIVE;
    }

    public final boolean isDead() {
        return playerState == PlayerState.DEAD;
    }
}
