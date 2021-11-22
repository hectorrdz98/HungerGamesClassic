package dev.sasukector.hungergamesclassic;

import dev.sasukector.hungergamesclassic.commands.GameCommand;
import dev.sasukector.hungergamesclassic.events.GameEvents;
import dev.sasukector.hungergamesclassic.events.SpawnEvents;
import dev.sasukector.hungergamesclassic.helpers.ServerUtilities;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class HungerGamesClassic extends JavaPlugin {

    private static @Getter HungerGamesClassic instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info(ChatColor.DARK_PURPLE + "HungerGamesClassic startup!");
        instance = this;

        // Register events
        this.getServer().getPluginManager().registerEvents(new SpawnEvents(), this);
        this.getServer().getPluginManager().registerEvents(new GameEvents(), this);

        // Register commands
        Objects.requireNonNull(HungerGamesClassic.getInstance().getCommand("game")).setExecutor(new GameCommand());

        // Set lobby spawn
        ServerUtilities.setLobbySpawn(new Location(ServerUtilities.getWorld("overworld"), 0, 100, 0));

        // Prepare worlds configuration
        Bukkit.getWorlds().forEach(world -> {
            world.setDifficulty(Difficulty.HARD);
            world.setGameRuleValue("doDaylightCycle", "false");
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info(ChatColor.DARK_PURPLE + "HungerGamesClassic shutdown!");
    }
}
