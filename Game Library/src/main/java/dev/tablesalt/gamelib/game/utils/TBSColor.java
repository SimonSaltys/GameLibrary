package dev.tablesalt.gamelib.game.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.mineacademy.fo.remain.CompMaterial;

public enum TBSColor {

	RED,
	BLUE,
	GREEN,
	YELLOW,
	AQUA,
	WHITE,
	PINK,
	PURPLE;

	public Color getColor() {
		return switch (this) {
			case RED -> Color.RED;
			case BLUE -> Color.BLUE;
			case GREEN -> Color.GREEN;
			case YELLOW -> Color.YELLOW;
			case AQUA -> Color.AQUA;
			case WHITE -> Color.WHITE;
			case PINK -> Color.FUCHSIA;
			case PURPLE -> Color.PURPLE;
		};
	}

	public ChatColor getChatColor() {
		return switch (this) {
			case RED -> ChatColor.RED;
			case BLUE -> ChatColor.BLUE;
			case GREEN -> ChatColor.GREEN;
			case YELLOW -> ChatColor.YELLOW;
			case AQUA -> ChatColor.AQUA;
			case WHITE -> ChatColor.WHITE;
			case PINK -> ChatColor.LIGHT_PURPLE;
			case PURPLE -> ChatColor.DARK_PURPLE;
		};

	}

	public TextColor getTextColor() {
		return switch (this) {
			case RED -> NamedTextColor.RED;
			case BLUE -> NamedTextColor.BLUE;
			case GREEN -> NamedTextColor.GREEN;
			case YELLOW -> NamedTextColor.YELLOW;
			case AQUA -> NamedTextColor.AQUA;
			case WHITE -> NamedTextColor.WHITE;
			case PINK -> NamedTextColor.LIGHT_PURPLE;
			case PURPLE -> NamedTextColor.DARK_PURPLE;
		};
	}

	public Material toWool() {
		return switch (this) {
			case RED -> Material.RED_WOOL;
			case BLUE -> Material.BLUE_WOOL;
			case GREEN -> Material.LIME_WOOL;
			case YELLOW -> Material.YELLOW_WOOL;
			case AQUA -> Material.CYAN_WOOL;
			case WHITE -> Material.WHITE_WOOL;
			case PINK -> Material.PINK_WOOL;
			case PURPLE -> Material.PURPLE_WOOL;
		};
	}

	public Material toConcrete() {
		Material wool = toWool();
		return CompMaterial.fromString(wool.toString().replace("_WOOL", "_CONCRETE")).toMaterial();
	}

	public Material toBanner() {
		Material wool = toWool();
		return CompMaterial.fromString(wool.toString().replace("_WOOL", "_BANNER")).toMaterial();
	}

	public Material toConcretePowder() {
		Material wool = toWool();
		return CompMaterial.fromString(wool.toString().replace("_WOOL", "_CONCRETE_POWDER")).toMaterial();
	}

	public Material toTerracotta() {
		Material wool = toWool();
		return CompMaterial.fromString(wool.toString().replace("_WOOL", "_TERRACOTTA")).toMaterial();
	}

	public Material toStainedGlass() {
		Material wool = toWool();
		return CompMaterial.fromString(wool.toString().replace("_WOOL", "_STAINED_GLASS")).toMaterial();
	}

	public Material toStainedGlassPane() {
		Material wool = toWool();
		return CompMaterial.fromString(wool.toString().replace("_WOOL", "_STAINED_GLASS_PANE")).toMaterial();
	}

	public Material toBed() {
		Material wool = toWool();

		return CompMaterial.fromString(wool.toString().replace("_WOOL", "_BED")).toMaterial();
	}
}