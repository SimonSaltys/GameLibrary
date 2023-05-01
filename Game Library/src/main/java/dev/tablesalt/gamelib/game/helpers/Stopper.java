package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.enums.GameState;
import dev.tablesalt.gamelib.game.utils.Message;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.model.BoxedMessage;
import org.mineacademy.fo.visual.VisualizedRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor()
public class Stopper {

    protected final Game game;

    private boolean stopping = false;

    public final void stop() {
        Valid.checkBoolean(!game.getState().isStopped(), "Cannot stop stopped game " + game.getName());
        stopping = true;

          try {
              cancelTasks();
              cleanEntities();
              broadcastStopMessageAndLeavePlayers();
              game.getScoreboard().onGameStop();

              try {
                  onGameStop();
              }catch (Throwable t) {
                  Common.error(t, "Could not properly stop game " + game.getName());

              }

          } catch (Throwable t) {
              Common.error(t,"Failed to stop game " + game.getName());
          } finally {
              game.getState().setState(GameState.STOPPED);
              game.clearPlayers();
              stopping = false;
          }
    }

    void onGameStop() {

    }

    public final boolean isStopping() {
        return stopping;
    }


    private void cancelTasks() {
        game.getStarter().stopIfStarting();
        game.getHeartbeat().stopIfBeating();
    }

    private void broadcastStopMessageAndLeavePlayers() {
        game.getPlayerGetter().forEachInAllModes(cache -> {
            Player player = cache.toPlayer();
            game.getPlayerLeaver().leavePlayer(player,Message.NO_MESSAGE);
            BoxedMessage.tell(player,"<center>&c&lGAME OVER\n\n<center>");
        });
    }


    private void cleanEntities() {
        VisualizedRegion region = game.getMapRotator().getCurrentMap().getRegion();
        final List<Entity> entities = region.isWhole() ? region.getEntities() : new ArrayList<>();
        final Set<String> ignoredEntities = Common.newSet("PLAYER", "ITEM_FRAME", "PAINTING", "ARMOR_STAND", "LEASH_HITCH");

        for (final Entity entity : entities)
            if (!ignoredEntities.contains(entity.getType().toString()))
                entity.remove();
    }
}
