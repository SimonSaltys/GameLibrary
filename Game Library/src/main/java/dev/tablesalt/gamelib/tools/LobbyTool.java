package dev.tablesalt.gamelib.tools;

import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.map.GameMap;
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
import org.mineacademy.fo.visual.VisualTool;
import org.mineacademy.fo.visual.VisualizedRegion;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)

public final class LobbyTool extends GameTool<Game> {

    @Getter
    private static final LobbyTool instance = new LobbyTool();

    @Override
    protected void onSuccessfulBlockClick(Player player, Game game, Block block, ClickType type) {
        boolean isPrimaryClick = (type == ClickType.LEFT);
        GameMap map = game.getMapRotator().getCurrentMap();
        VisualizedRegion region = map.getLobbyRegion();

        if (isPrimaryClick)
            region.updateLocation(block.getLocation(), null);
        else
            region.updateLocation(null,block.getLocation());

        map.save();
    }

    @Override
    protected VisualizedRegion getVisualizedRegion(Player player) {
        Game game = PlayerCache.from(player).getGameIdentifier().getCurrentGame();
        PlayerCache cache = PlayerCache.from(player);
        if(cache.getGameIdentifier().hasGame())
            return game.getMapRotator().getCurrentMap().getLobbyRegion();

        return null;
    }

    @Override
    protected String getBlockName(Block block, Player player) {
        return "&l[&fLobby point&l]";
    }

    @Override
    protected CompMaterial getBlockMask(Block block, Player player) {
        return CompMaterial.GLOWSTONE;
    }

    @Override
    public ItemStack getItem() {
        return ItemCreator.of(CompMaterial.BEETROOT_SEEDS,"&l&3LOBBY TOOL",
                "",
                "Use to set region points",
                "for an edited game.",
                "",
                "&b<< &fLeft click &7– &fPrimary",
                "&fRight click &7– &fSecondary &b>>").makeMenuTool();
    }
}
