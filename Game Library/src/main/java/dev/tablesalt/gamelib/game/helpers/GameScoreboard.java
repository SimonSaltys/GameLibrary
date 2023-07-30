package dev.tablesalt.gamelib.game.helpers;

import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.map.GameMap;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.model.SimpleScoreboard;
import org.mineacademy.fo.region.Region;

public class GameScoreboard extends SimpleScoreboard {

    private final Game game;

    public GameScoreboard(Game game) {
        this.game = game;
        setTitle("&8----- &f" + game.getName() + " &8-----");
        setTheme(ChatColor.WHITE,ChatColor.GRAY);
        setUpdateDelayTicks(20);
    }


    @Override
    protected final String replaceVariables(@NonNull Player player, @NonNull String message) {
        int timeToStart = game.getStarter().getTimeLeft();
        int timeToEnd = game.getHeartbeat().getTimeLeft();
        GameMap currentMap = game.getMapRotator().getCurrentMap();
        Region region = currentMap.getRegion();

      message = Replacer.replaceArray(message,"remaining_start", timeToStart > 0 ? Common.plural(timeToStart,"seconds") : "Waiting...",
              "remaining_end", TimeUtil.formatTimeShort(timeToEnd),

              "players", GameUtil.generateColoredGradientNumerical(game.getMaxPlayers(),game.getPlayerGetter()
                      .getPlayers(game.getState().isEdited() ? GameJoinMode.EDITING : GameJoinMode.PLAYING).size()),

              "min_players", game.getMinPlayers(),
              "state", ItemUtil.bountifyCapitalized(game.getState().toString()),
              "lobby_set", currentMap.getLobbyLocation() != null,
              "region_set", region != null && region.isWhole(),
              "is_setup", currentMap.isSetup());

      message = replaceVariablesLate(player,message);

      return message.replace("true", "&aYes").replace("false","&4No");
    }

    protected String replaceVariablesLate(Player player, String message) { return message; }

    public final void onPlayerJoin(Player player) {
        SimpleScoreboard.clearBoardsFor(player);

        show(player);
    }

    public final void onPlayerLeave(Player player) {
        SimpleScoreboard.clearBoardsFor(player);

        if (isViewing(player))
            hide(player);
    }

    public final void onLobbyStart() {
        addRows("",
                "Players: {players}",
                "Starting in: {remaining_start}",
                "State: {state}");
        addLobbyRows();
    }

    protected void addLobbyRows() {

    }

    public final void onEditStart() {
        addRows("",
                "&cEditing players: {players}",
                "");

        this.addEditRows();

        addRows( "",
                "&cLobby: {lobby_set}",
                "&cRegion: {region_set}",
                "&cIs setup: {is_setup}");

        this.addRows("",
                "&7Use: /game tools to edit.");
    }

    public void addEditRows() {
    }

    public final void onGameStart() {
        this.removeRow("Starting in");
        this.addRows("Time left: {remaining_end}");
        this.addStartRows();
    }

    public void addStartRows() {

    }

    public final void onGameStop() {
        clearRows();
        stop();
    }


}
