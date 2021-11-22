package dev.sasukector.hungergamesclassic.helpers;

import com.google.common.io.Files;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ServerUtilities {

    private static @Getter @Setter Location lobbySpawn = null;

    // Associate all world names
    public final static @Getter Map<String, String> worldsNames;
    static {
        worldsNames = new HashMap<>();
        worldsNames.put("overworld", "world");
        worldsNames.put("nether", "world_nether");
        worldsNames.put("end", "world_the_end");
    }

    public static String getPluginNameColored() {
        return "§d§lHungerGames";
    }

    public static void sendBroadcastMessage(String message) {
        Bukkit.broadcastMessage(getPluginNameColored() + " §7§l▶ §r" + message);
    }

    public static void sendBroadcastTitle(String title, String subtitle) {
        Title newTitle = new Title(title, subtitle);
        Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(newTitle));
    }

    public static void sendServerMessage(Player player, String message) {
        player.sendMessage(getPluginNameColored() + " §7§l▶ §r" + message);
    }

    public static void playBroadcastSound(String sound, float volume, float pitch) {
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), sound, volume, pitch));
    }

    public static World getWorld(String worldAlias) {
        if (worldsNames.containsKey(worldAlias)) {
            return Bukkit.getWorld(worldsNames.get(worldAlias));
        }
        return null;
    }

    public static void copyFile(String from, String to) throws IOException {
        Path src = Paths.get(from);
        Path dest = Paths.get(to);
        Files.copy(src.toFile(), dest.toFile());
    }

}
