package dev.tablesalt.gamelib.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;
@AutoRegister
public final class GameCommandGroup extends SimpleCommandGroup {

    @Getter(value = AccessLevel.PRIVATE)
    private static final GameCommandGroup instance = new GameCommandGroup();

    @Override
    protected String getCredits() {
        return "todo";
    }

    @Override
    protected void registerSubcommands() {
        registerSubcommand(GameSubCommand.class);
        registerSubcommand(new ReloadCommand());

    }
}
