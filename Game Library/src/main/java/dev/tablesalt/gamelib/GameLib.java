package dev.tablesalt.gamelib;

import lombok.Getter;
import org.mineacademy.fo.plugin.SimplePlugin;

public abstract class GameLib extends SimplePlugin {

    private static GameLib instance;


    public GameLib() {
        instance = this;
    }


    @Override
    public void onPluginStart() {
    }

    @Override
    protected void onReloadablesStart() {
    }

    @Override
    public void onPluginStop() {
        // Plugin shutdown logic
    }

    public static GameLib getInstance() {
        return instance;
    }
}
