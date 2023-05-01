package dev.tablesalt.gamelib.tools;

import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)

public final class LobbyTool extends GameTool<Game>{

    @Getter
    private static final LobbyTool instance = new LobbyTool();

    @Override
    protected void onSuccessfulBlockClick(Player player, Game game, Block block, ClickType type) {
        game.getMapRotator().getCurrentMap().setLobbyLocation(block.getLocation());

        Messenger.success(player,"Lobby set: &l" + Common.shortLocation(block.getLocation()));
    }

    @Override
    protected Location getGamePoint(Player player, Game game) {

        PlayerCache cache = PlayerCache.from(player);

        if(cache.getGameIdentifier().hasGame())
            return game.getMapRotator().getCurrentMap().getLobbyLocation();

        return null;
    }

    @Override
    protected String getBlockName(Block block, Player player) {
        return "&l[&fLobby Point&l]";
    }

    @Override
    protected CompMaterial getBlockMask(Block block, Player player) {
        return CompMaterial.GLOWSTONE;
    }

    @Override
    public ItemStack getItem() {
        return ItemCreator.of(CompMaterial.SHEARS,"&l&3LOBBY TOOL",
                "",
                "Use to set lobby point.").makeMenuTool();
    }
}
