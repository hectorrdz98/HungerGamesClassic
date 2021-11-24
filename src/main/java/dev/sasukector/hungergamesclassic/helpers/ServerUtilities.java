package dev.sasukector.hungergamesclassic.helpers;

import com.google.common.io.Files;
import dev.sasukector.hungergamesclassic.HungerGamesClassic;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static void sendBroadcastAction(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> sendActionBar(player, message));
    }

    public static void sendActionBar(Player player, String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        try {
            Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + HungerGamesClassic.v + ".entity.CraftPlayer");
            Object p = c1.cast(player);
            Object ppoc;
            Class<?> c4 = Class.forName("net.minecraft.server." + HungerGamesClassic.v + ".PacketPlayOutChat");
            Class<?> c5 = Class.forName("net.minecraft.server." + HungerGamesClassic.v + ".Packet");

            Class<?> c2 = Class.forName("net.minecraft.server." + HungerGamesClassic.v + ".ChatComponentText");
            Class<?> c3 = Class.forName("net.minecraft.server." + HungerGamesClassic.v + ".IChatBaseComponent");
            Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
            ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);

            Method getHandle = c1.getDeclaredMethod("getHandle");
            Object handle = getHandle.invoke(p);

            Field fieldConnection = handle.getClass().getDeclaredField("playerConnection");
            Object playerConnection = fieldConnection.get(handle);

            Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", c5);
            sendPacket.invoke(playerConnection, ppoc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static World getWorld(String worldAlias) {
        if (worldsNames.containsKey(worldAlias)) {
            return Bukkit.getWorld(worldsNames.get(worldAlias));
        }
        return null;
    }

    public static Location getSafeLocation(Location location) {
        List<Integer> ys = Stream.iterate(2, n -> n + 1).limit(100)
                .sorted(Collections.reverseOrder()).collect(Collectors.toList());
        Location newLocation = null;
        for (int y : ys) {
            location.setY(y);
            Block cBlock = location.getBlock();
            Block tBlock = location.add(0, 1, 0).getBlock();
            Block lBlock = location.add(0, -2, 0).getBlock();
            if (cBlock.getType() == Material.WATER && tBlock.getType() == Material.WATER &&
                    lBlock.getType().isSolid() && lBlock.getType() != Material.BARRIER
            ) {
                location.setY(y);
                newLocation = location;
                break;
            } else if (cBlock.getType() == Material.AIR && tBlock.getType() == Material.AIR &&
                    lBlock.getType().isSolid() && lBlock.getType() != Material.BARRIER
            ) {
                location.setY(y);
                newLocation = location;
                break;
            }
        }
        return newLocation;
    }

    public static void copyFile(String from, String to) throws IOException {
        Path src = Paths.get(from);
        Path dest = Paths.get(to);
        Files.copy(src.toFile(), dest.toFile());
    }

}
