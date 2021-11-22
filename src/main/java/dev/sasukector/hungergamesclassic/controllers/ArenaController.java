package dev.sasukector.hungergamesclassic.controllers;

import dev.sasukector.hungergamesclassic.models.Arena;
import lombok.Getter;
import org.bukkit.Location;

import java.util.*;

public class ArenaController {

    private static ArenaController instance = null;
    private final @Getter Map<String, Arena> arenas;
    private final Random random = new Random();

    public static ArenaController getInstance() {
        if (instance == null) {
            instance = new ArenaController();
        }
        return instance;
    }

    public ArenaController() {
        this.arenas = new HashMap<>();
        this.createArenas();
    }

    public void createArenas() {
        Arena arena1 = new Arena("hg_arena_1", new int[]{-3, 74, -3}, 30, 200);
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 0, 64, 10));
        this.arenas.put("hg_arena_1", arena1);

        Arena arena2 = new Arena("hg_arena_2", new int[]{0, 74, 0}, 30, 200);
        arena2.getChestLocations().add(new Location(arena2.getWorld(), 0, 64, 10));
        this.arenas.put("hg_arena_2", arena2);
    }

    public Arena getRandomArena() {
        List<Arena> loadedArenas = new ArrayList<>(this.arenas.values());
        return loadedArenas.get(random.nextInt(loadedArenas.size()));
    }

}
