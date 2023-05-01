package dev.tablesalt.gamelib.tools;

import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.map.GameMap;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.visual.VisualizedRegion;

import java.util.List;
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegionTool extends GameTool<Game> {

    @Getter
    private static final RegionTool instance = new RegionTool();

    @Override
    protected void onSuccessfulBlockClick(Player player, Game game, Block block, ClickType type) {
        boolean isPrimaryClick = (type == ClickType.LEFT);
        GameMap map = game.getMapRotator().getCurrentMap();
        VisualizedRegion region = map.getRegion();


        if (isPrimaryClick)
            region.updateLocation(block.getLocation(), null);
        else
            region.updateLocation(null,block.getLocation());

    }

    @Override
    protected VisualizedRegion getVisualizedRegion(Player player) {
        Game game = PlayerCache.from(player).getGameIdentifier().getCurrentGame();
        PlayerCache cache = PlayerCache.from(player);

        if(cache.getGameIdentifier().hasGame())
            return game.getMapRotator().getCurrentMap().getRegion();

        return null;
    }

    @Override
    protected String getBlockName(Block block, Player player) {
        return "&l[&fRegion point&l]";
    }

    @Override
    protected CompMaterial getBlockMask(Block block, Player player) {
        return CompMaterial.HAY_BLOCK;
    }

    @Override
    public ItemStack getItem() {
        return ItemCreator.of(CompMaterial.WHEAT,"&l&3REGION TOOL",
                "",
                "Use to set region points",
                "for an edited game.",
                "",
                "&b<< &fLeft click &7– &fPrimary",
                "&fRight click &7– &fSecondary &b>>").makeMenuTool();
    }
}
