package dev.tablesalt.gamelib.tools;

import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.visual.VisualTool;

import java.util.List;

public abstract class GameTool<T extends Game> extends VisualTool {

    private static final Class<Game> APPLICABLE_TO_ALL_GAMES = null;

    protected static final CompMaterial NO_ITEM = CompMaterial.AIR;

    @Getter
    protected final Class<Game> gameClass;

    protected GameTool() { this.gameClass = APPLICABLE_TO_ALL_GAMES; }

    /**
     * The game that the player is editing, needs to be of the same type as specified as T in this class
     * @return the current edited game
     */
    protected final T getCurrentGame(Player player) {
        return (T) PlayerCache.from(player).getGameIdentifier().getCurrentGame();
    }

    /**
     * The point where the data has been set by the player
     */
    protected Location getGamePoint(Player player, T game) {
        return null;
    }


    /**
     * Any other additional points you want to be visualized
     */
    protected List<Location> getGamePoints(Player player, T game) {
        return null;
    }

    @Override
    protected final void handleBlockClick(Player player, ClickType click, Block block) {
        Game game = getEditedGame(player);

       if (!isGameEditable(game,player))
           return;

       super.handleBlockClick(player,click,block);
       this.onSuccessfulBlockClick(player, (T) game, block, click);
    }

    protected void onSuccessfulBlockClick(Player player, T game, Block block, ClickType type) {
    }

    @Override
    protected void handleAirClick(Player player, ClickType click) {
        Game game = getEditedGame(player);

        if (!isGameEditable(game,player))
            return;

        this.onSuccessfulAirClick(player,(T) game,click);

    }

    protected void onSuccessfulAirClick(Player player, T game, ClickType type) {
    }

    private Game getEditedGame(Player player) {
        PlayerCache cache = PlayerCache.from(player);

        return cache.getGameIdentifier().hasGame() && cache.getMode() == GameJoinMode.EDITING
                ? cache.getGameIdentifier().getCurrentGame() : null;
    }

    private boolean isGameEditable(Game game, Player player) {
        if (game == null) {
            Messenger.error(player,"You must be editing a game to use this tool.");
            return false;
        }

        if (!game.getState().isEdited()) {
            Messenger.error(player,"The game " + game.getName() + " must be in edit mode to edit.");
            return false;
        }
        return true;
    }

    @Override
    protected final List<Location> getVisualizedPoints(Player player) {
        List<Location> points = super.getVisualizedPoints(player);
        T game = getCurrentGame(player);

        if (game != null) {
            Location point = getGamePoint(player,game);

            if (point != null)
                points.add(point);

            List<Location> additionalPoints = getGamePoints(player,game);

            if (additionalPoints != null)
                points.addAll(additionalPoints);
        }
        return points;
    }

    public boolean isApplicable(Game game) {
        return gameClass == APPLICABLE_TO_ALL_GAMES || gameClass.isAssignableFrom(game.getClass());
    }

}
