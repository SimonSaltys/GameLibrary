package dev.tablesalt.gamelib.game.utils;

import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.types.Type;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.io.File;
import java.io.FileFilter;

@UtilityClass
public class GameFile {

    public File getFile(String path) {
       return new File(path);
    }

    /**
     * @param directory The directory where the files are located
     * @param extension The extension of the files to find, for example "yml"
     * @return All the found files
     *
     * Modified by Simon Saltikov
     */
    public static File[] getFiles(@NonNull String directory, @NonNull String extension) {

        // Remove initial dot, if any
        if (extension.startsWith("."))
            extension = extension.substring(1);

        final File dataFolder = new File(SimplePlugin.getData(),directory);

        if (!dataFolder.exists())
            dataFolder.mkdirs();

        final String finalExtension = extension;

        return dataFolder.listFiles(file -> !file.isDirectory() && file.getName().endsWith("." + finalExtension));
    }
    public File getGamesFolder() {
        return getFile("games");
    }

    public File getGamesTypeFolder(Type<Game> type) {
        return getFile("games/" + type.getName());
    }

    public File getGamesMapFolder(Game game) {
        return getFile("games/" + game.getType().getName() + "/" + game.getName() + "_maps");
    }

    public File getMapFile(String name, Game game) {
        File mapFolder = getGamesMapFolder(game);
        return getFile(mapFolder.getPath() + "/" + name + ".yml");
    }
}
