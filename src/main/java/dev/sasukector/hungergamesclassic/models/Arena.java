package dev.sasukector.hungergamesclassic.models;

import dev.sasukector.hungergamesclassic.controllers.GameController;
import dev.sasukector.hungergamesclassic.helpers.ServerUtilities;
import lombok.Getter;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Arena {

    private @Getter World world;
    private final @Getter String name;
    private final @Getter int[] spawnLocation;
    private final @Getter List<Location> chestLocations;
    private final @Getter int lobbyRadius;
    private final @Getter int maxRadius;

    public Arena(String name, int[] spawnLocation, int lobbyRadius, int maxRadius) {
        this.name = name;
        this.spawnLocation = spawnLocation;
        this.lobbyRadius = lobbyRadius;
        this.maxRadius = maxRadius;
        this.chestLocations = new ArrayList<>();
        this.world = null;
    }

    public void fillChests() {

    }

    public void teleportPlayers() {
        Location location = new Location(this.world, this.spawnLocation[0], this.spawnLocation[1], this.spawnLocation[2]);
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.teleport(location);
            if (!GameController.getInstance().getAlivePlayers().contains(player.getUniqueId())) {
                player.setGameMode(GameMode.SPECTATOR);
                ServerUtilities.sendServerMessage(player, "§7Observarás la partida");
            }
        });
    }

    public void teleportPlayer(Player player) {
        player.teleport(new Location(this.world, this.spawnLocation[0], this.spawnLocation[1], this.spawnLocation[2]));
    }

    public void configureWorld() {
        if (this.world != null) {
            this.deleteWorld();
        }
        if (this.copyBaseMapToLobbyMap()) {
            if (this.unZipLobbyMap()) {
                this.world = Bukkit.getServer().createWorld(new WorldCreator(this.name));
                this.world.setDifficulty(Difficulty.HARD);
                this.world.setGameRuleValue("doDaylightCycle", "false");
                this.world.setGameRuleValue("doWeatherCycle", "false");
            }
        }
    }

    private boolean copyBaseMapToLobbyMap() {
        boolean correct = false;
        try {
            // Create lobby directory
            File lobbyDir = new File(this.name);
            if (lobbyDir.exists()) {
                Bukkit.getLogger().info(ChatColor.RED + "World for lobby " + this.name + " already exists");
                this.deleteWorld();
            }
            if (lobbyDir.mkdir()) {
                Bukkit.getLogger().info(ChatColor.AQUA + "Created world for lobby " + this.name);
                // Copy zip file to lobby directory
                ServerUtilities.copyFile("maps/" + this.name + ".zip", lobbyDir + "/map.zip");
                Bukkit.getLogger().info(ChatColor.AQUA + "Success coping world to lobby's folder");
                correct = true;
            } else {
                Bukkit.getLogger().info(ChatColor.RED + "Function mkdir() failed for lobby " + this.name);
            }
        } catch (Exception e) {
            Bukkit.getLogger().info(ChatColor.RED + "Error while copyBaseMapToLobbyMap(): " + e);
            e.printStackTrace();
        }

        return correct;
    }

    private boolean unZipLobbyMap() {
        boolean correct = false;
        String zipFilePath = this.name + "/map.zip";
        try {
            // Unzip world
            ZipFile zipFile = new ZipFile(zipFilePath);
            zipFile.extractAll(this.name);
            Bukkit.getLogger().info(ChatColor.AQUA + "Success unzipping world");
            // Delete zip file
            File originalWorldZip = new File(zipFilePath);
            if (originalWorldZip.delete()) {
                Bukkit.getLogger().info(ChatColor.AQUA + "Success deleting zip file");
            } else {
                Bukkit.getLogger().info(ChatColor.RED + "Failed to delete zip file");
            }
            correct = true;
        } catch (Exception e) {
            Bukkit.getLogger().info(ChatColor.RED + "Error while unZipLobbyMap(): " + e);
            e.printStackTrace();
        }
        return correct;
    }

    public void deleteWorld() {
        if (this.world != null) {
            Bukkit.getServer().unloadWorld(this.name, true);
        }
        File dir = new File(this.name);
        try {
            FileUtils.deleteDirectory(dir);
        } catch (Exception e) {
            Bukkit.getLogger().info(ChatColor.RED + "Error while deleteWorld(): " + e);
            e.printStackTrace();
        }
    }

}
