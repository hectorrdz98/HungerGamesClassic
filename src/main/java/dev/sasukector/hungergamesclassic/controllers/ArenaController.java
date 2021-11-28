package dev.sasukector.hungergamesclassic.controllers;

import dev.sasukector.hungergamesclassic.models.Arena;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.*;

public class ArenaController {

    private static ArenaController instance = null;
    private final @Getter Map<String, Arena> arenas;
    private final @Getter List<ItemStack> chestItems;
    private final Random random = new Random();

    public static ArenaController getInstance() {
        if (instance == null) {
            instance = new ArenaController();
        }
        return instance;
    }

    public ArenaController() {
        this.arenas = new HashMap<>();
        this.chestItems = new ArrayList<>();
        this.createArenas();
        this.createRandomChestContents();
    }

    public void createArenas() {
        Arena arena1 = new Arena("hg_arena_1", new int[]{0, 75, 0}, 60, 1000, 300);
        // Spawn chests
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 3, 69, -5));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 2, 69, -6));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 0, 69, -6));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -2, 69, -6));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -3, 69, -5));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -5, 69, -3));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -6, 69, -2));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -6, 69, 0));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -6, 69, 2));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -5, 69, 3));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -3, 69, 5));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -2, 69, 6));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 0, 69, 6));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 2, 69, 6));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 3, 69, 5));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 5, 69, 3));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 6, 69, 2));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 6, 69, 0));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 6, 69, -2));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 5, 69, -3));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 1, 70, -3));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -1, 70, -3));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -3, 70, -1));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -3, 70, 1));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -1, 70, 3));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 1, 70, 3));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 3, 70, 1));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 3, 70, -1));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 1, 71, -2));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -1, 71, -2));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -2, 71, -1));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -2, 71, 1));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -1, 71, 2));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 1, 71, 2));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 2, 71, 1));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 2, 71, -1));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 0, 72, -2));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -2, 72, 0));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 0, 72, 2));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 2, 72, 0));
        // Other chests
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 491, 75, 97));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 491, 69, 97));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 491, 59, 97));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 441, 67, 24));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 500, 68, -74));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 488, 74, -169));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 473, 74, -179));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 485, 78, -180));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 478, 64, -212));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 275, 61, 405));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 232, 113, 295));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 231, 93, 297));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 231, 59, -182));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 84, 130, -491));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 82, 122, -487));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 77, 72, -489));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 44, 78, -328));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 65, 59, -246));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 51, 72, -169));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 116, 64, -111));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 109, 61, -118));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 123, 71, 162));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 18, 66, 320));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -378, 67, 456));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -440, 101, 384));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -394, 85, 81));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -466, 64, -48));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -497, 98, -140));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -486, 90, -140));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -472, 71, -123));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -484, 78, -116));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -471, 78, -114));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -454, 97, -228));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -362, 138, -384));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -105, 77, -376));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -30, 106, -395));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -89, 66, -165));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -191, 39, -116));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -228, 92, -38));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), 191, 123, 218));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -427, 62, -25));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -427, 63, -37));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -422, 62, -30));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -432, 62, -30));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -485, 78, -117));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -472, 78, -115));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -106, 77, -377));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -105, 77, -376));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -487, 90, -141));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -477, 98, -141));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -473, 71, -124));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -363, 138, -385));
        arena1.getChestLocations().add(new Location(arena1.getWorld(), -455, 97, -229));
        this.arenas.put("hg_arena_1", arena1);
    }

    public void createRandomChestContents() {
        // Food
        this.chestItems.add(new ItemStack(Material.COOKED_MUTTON));
        this.chestItems.add(new ItemStack(Material.MUTTON));
        this.chestItems.add(new ItemStack(Material.COOKED_BEEF));
        this.chestItems.add(new ItemStack(Material.RAW_BEEF));
        this.chestItems.add(new ItemStack(Material.COOKED_CHICKEN));
        this.chestItems.add(new ItemStack(Material.RAW_CHICKEN));
        this.chestItems.add(new ItemStack(Material.COOKED_FISH));
        this.chestItems.add(new ItemStack(Material.RAW_FISH));
        this.chestItems.add(new ItemStack(Material.APPLE));
        this.chestItems.add(new ItemStack(Material.GOLDEN_APPLE));
        this.chestItems.add(new ItemStack(Material.CARROT));
        this.chestItems.add(new ItemStack(Material.GOLDEN_CARROT));
        this.chestItems.add(new ItemStack(Material.BREAD));
        this.chestItems.add(new ItemStack(Material.WHEAT));
        // Random
        this.chestItems.add(new ItemStack(Material.STICK));
        this.chestItems.add(new ItemStack(Material.SADDLE));
        this.chestItems.add(new ItemStack(Material.BUCKET));
        this.chestItems.add(new ItemStack(Material.WATER_BUCKET));
        this.chestItems.add(new ItemStack(Material.LAVA_BUCKET));
        this.chestItems.add(new ItemStack(Material.MILK_BUCKET));
        this.chestItems.add(new ItemStack(Material.SNOW_BALL));
        this.chestItems.add(new ItemStack(Material.BONE));
        this.chestItems.add(new ItemStack(Material.ENDER_PEARL));
        this.chestItems.add(new ItemStack(Material.EGG));
        this.chestItems.add(new ItemStack(Material.EXP_BOTTLE));
        this.chestItems.add(new ItemStack(Material.BOOK));
        this.chestItems.add(new ItemStack(Material.BOOKSHELF));
        // Minerals
        this.chestItems.add(new ItemStack(Material.COAL));
        this.chestItems.add(new ItemStack(Material.IRON_INGOT));
        this.chestItems.add(new ItemStack(Material.GOLD_INGOT));
        this.chestItems.add(new ItemStack(Material.DIAMOND));
        this.chestItems.add(new ItemStack(Material.REDSTONE));
        // Tools
        this.chestItems.add(new ItemStack(Material.FISHING_ROD));
        this.chestItems.add(new ItemStack(Material.SHEARS));
        this.chestItems.add(new ItemStack(Material.FLINT_AND_STEEL));
        this.chestItems.add(new ItemStack(Material.ANVIL));
        this.chestItems.add(new ItemStack(Material.TNT));
        this.chestItems.add(new ItemStack(Material.REDSTONE));
        this.chestItems.add(new ItemStack(Material.LAPIS_ORE));
        this.chestItems.add(new ItemStack(Material.WOOD_AXE));
        this.chestItems.add(new ItemStack(Material.WOOD_HOE));
        this.chestItems.add(new ItemStack(Material.WOOD_PICKAXE));
        this.chestItems.add(new ItemStack(Material.WOOD_SPADE));
        this.chestItems.add(new ItemStack(Material.STONE_AXE));
        this.chestItems.add(new ItemStack(Material.STONE_HOE));
        this.chestItems.add(new ItemStack(Material.STONE_PICKAXE));
        this.chestItems.add(new ItemStack(Material.STONE_SPADE));
        this.chestItems.add(new ItemStack(Material.IRON_AXE));
        this.chestItems.add(new ItemStack(Material.IRON_HOE));
        this.chestItems.add(new ItemStack(Material.IRON_PICKAXE));
        this.chestItems.add(new ItemStack(Material.IRON_SPADE));
        this.chestItems.add(new ItemStack(Material.GOLD_AXE));
        this.chestItems.add(new ItemStack(Material.GOLD_HOE));
        this.chestItems.add(new ItemStack(Material.GOLD_PICKAXE));
        this.chestItems.add(new ItemStack(Material.GOLD_SPADE));
        // Weapons
        this.chestItems.add(new ItemStack(Material.WOOD_SWORD));
        this.chestItems.add(new ItemStack(Material.STONE_SWORD));
        this.chestItems.add(new ItemStack(Material.IRON_SWORD));
        this.chestItems.add(new ItemStack(Material.GOLD_SWORD));
        this.chestItems.add(new ItemStack(Material.POTION));
        this.chestItems.add(new ItemStack(Material.ENCHANTED_BOOK));
        this.chestItems.add(new ItemStack(Material.BOW));
        this.chestItems.add(new ItemStack(Material.ARROW));
        // Armor
        this.chestItems.add(new ItemStack(Material.LEATHER_HELMET));
        this.chestItems.add(new ItemStack(Material.LEATHER_CHESTPLATE));
        this.chestItems.add(new ItemStack(Material.LEATHER_LEGGINGS));
        this.chestItems.add(new ItemStack(Material.LEATHER_BOOTS));
        this.chestItems.add(new ItemStack(Material.IRON_HELMET));
        this.chestItems.add(new ItemStack(Material.IRON_CHESTPLATE));
        this.chestItems.add(new ItemStack(Material.IRON_LEGGINGS));
        this.chestItems.add(new ItemStack(Material.IRON_BOOTS));
        this.chestItems.add(new ItemStack(Material.CHAINMAIL_HELMET));
        this.chestItems.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        this.chestItems.add(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        this.chestItems.add(new ItemStack(Material.CHAINMAIL_BOOTS));
        this.chestItems.add(new ItemStack(Material.GOLD_HELMET));
        this.chestItems.add(new ItemStack(Material.GOLD_CHESTPLATE));
        this.chestItems.add(new ItemStack(Material.GOLD_LEGGINGS));
        this.chestItems.add(new ItemStack(Material.GOLD_BOOTS));
    }

    public PotionType getRandomPotionType() {
        switch (random.nextInt(14)) {
            case 0: return PotionType.POISON;
            case 1: return PotionType.FIRE_RESISTANCE;
            case 2: return PotionType.INSTANT_DAMAGE;
            case 3: return PotionType.INSTANT_HEAL;
            case 4: return PotionType.INVISIBILITY;
            case 5: return PotionType.JUMP;
            case 6: return PotionType.NIGHT_VISION;
            case 7: return PotionType.REGEN;
            case 8: return PotionType.SLOWNESS;
            case 9: return PotionType.SPEED;
            case 10: return PotionType.STRENGTH;
            case 11: return PotionType.WATER;
            case 12: return PotionType.WATER_BREATHING;
            case 13: return PotionType.WEAKNESS;
        }
        return null;
    }

    public Enchantment getRandomEnchantment() {
        switch (random.nextInt(25)) {
            case 0: return Enchantment.ARROW_INFINITE;
            case 1: return Enchantment.ARROW_DAMAGE;
            case 2: return Enchantment.ARROW_FIRE;
            case 3: return Enchantment.ARROW_KNOCKBACK;
            case 4: return Enchantment.DAMAGE_ALL;
            case 5: return Enchantment.DAMAGE_ARTHROPODS;
            case 6: return Enchantment.DAMAGE_UNDEAD;
            case 7: return Enchantment.DEPTH_STRIDER;
            case 8: return Enchantment.DIG_SPEED;
            case 9: return Enchantment.DURABILITY;
            case 10: return Enchantment.FIRE_ASPECT;
            case 11: return Enchantment.KNOCKBACK;
            case 12: return Enchantment.LOOT_BONUS_BLOCKS;
            case 13: return Enchantment.LOOT_BONUS_MOBS;
            case 14: return Enchantment.LUCK;
            case 15: return Enchantment.LURE;
            case 16: return Enchantment.OXYGEN;
            case 17: return Enchantment.PROTECTION_ENVIRONMENTAL;
            case 18: return Enchantment.PROTECTION_EXPLOSIONS;
            case 19: return Enchantment.PROTECTION_FALL;
            case 20: return Enchantment.PROTECTION_FIRE;
            case 21: return Enchantment.PROTECTION_PROJECTILE;
            case 22: return Enchantment.SILK_TOUCH;
            case 23: return Enchantment.THORNS;
            case 24: return Enchantment.WATER_WORKER;
        }
        return null;
    }

    public Arena getRandomArena() {
        List<Arena> loadedArenas = new ArrayList<>(this.arenas.values());
        return loadedArenas.get(random.nextInt(loadedArenas.size()));
    }

}
