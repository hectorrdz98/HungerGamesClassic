package dev.sasukector.hungergamesclassic.controllers;

import dev.sasukector.hungergamesclassic.HungerGamesClassic;
import dev.sasukector.hungergamesclassic.helpers.ServerUtilities;
import dev.sasukector.hungergamesclassic.models.Kit;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Dye;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class KitController {

    private static KitController instance = null;
    private final @Getter List<Kit> kitList;
    private final @Getter Map<UUID, Kit> playersKits;
    private final @Getter Map<UUID, Integer> playersTimers;
    private final @Getter Map<UUID, Integer> enderpearlTimers;
    private static final Random random = new Random();
    private final @Getter Inventory inventory;

    public static KitController getInstance() {
        if (instance == null) {
            instance = new KitController();
        }
        return instance;
    }

    public KitController() {
        this.kitList = new ArrayList<>();
        this.playersKits = new HashMap<>();
        this.playersTimers = new HashMap<>();
        this.enderpearlTimers = new HashMap<>();
        this.inventory = Bukkit.createInventory(null, 18, "§dSeleccionar Kit");
        this.createKits();
        this.fillInventory();
        this.reducePlayerTimers();
    }

    public void givePlayersKitSelector() {
        ItemStack book = new ItemStack(Material.BOOK);
        book.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName("§dSeleccionar Kit");
        bookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        book.setItemMeta(bookMeta);
        GameController.getInstance().getAlivePlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.getInventory().clear();
                player.getEquipment().setArmorContents(new ItemStack[]{ null, null, null, null });
                player.getInventory().addItem(book.clone());
            }
        });
    }

    public void givePlayersKits() {
        GameController.getInstance().getAlivePlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                Kit playerKit = this.playersKits.get(player.getUniqueId());
                if (playerKit == null) {
                    playerKit = this.kitList.get(random.nextInt(this.kitList.size()));
                    ServerUtilities.sendServerMessage(player, "No tienes un kit seleccionado, se asignó " +
                            playerKit.getColor() + "§l" + playerKit.getName() + " §rpor defecto");
                }
                player.closeInventory();
                player.getInventory().clear();
                player.getEquipment().setArmorContents(playerKit.getArmorContents().clone());
                PlayerInventory playerInventory = player.getInventory();
                for (ItemStack itemStack : playerKit.getKitContents()) {
                    playerInventory.addItem(itemStack.clone());
                }
                player.updateInventory();
                for (PotionEffectType effectType : playerKit.getEffects()) {
                    player.addPotionEffect(new PotionEffect(effectType, 99999, 0, false, false));
                }
            }
        });
    }

    public Kit getKit(String name) {
        Kit foundKit = null;
        Optional<Kit> optionalKit = this.kitList.stream()
                .filter(kit -> (kit.getColor() + kit.getName()).equals(name)).findFirst();
        if (optionalKit.isPresent()) {
            foundKit = optionalKit.get();
        }
        return foundKit;
    }

    public void reducePlayerTimers() {
        Bukkit.getScheduler().runTaskTimer(HungerGamesClassic.getInstance(), () -> {
            reduceTimer(playersTimers);
            reduceTimer(enderpearlTimers);
        }, 0L, 20L);
    }

    public void reduceTimer(Map<UUID, Integer> timers) {
        List<UUID> uuids = new ArrayList<>(timers.keySet());
        for (UUID timerUUID : uuids) {
            if (Bukkit.getPlayer(timerUUID) == null) {
                timers.remove(timerUUID);
            } else {
                int newTimer = timers.get(timerUUID) - 1;
                timers.put(timerUUID, newTimer);
                if (newTimer <= 0) {
                    timers.remove(timerUUID);
                }
            }
        }
    }

    private void createKits() {
        ItemStack trackingCompass = new ItemStack(Material.COMPASS);
        trackingCompass.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta trackingCompassMeta = trackingCompass.getItemMeta();
        trackingCompassMeta.setDisplayName("§dJugador cercano");
        trackingCompassMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        trackingCompass.setItemMeta(trackingCompassMeta);

        Kit enchanter = new Kit(Kit.KitID.ENCHANTER, "Enchanter", "§7", new ItemStack(Material.ENCHANTMENT_TABLE));
        enchanter.addItem(new ItemStack(Material.STONE_SWORD, 1));
        enchanter.addItem(new ItemStack(Material.COOKED_BEEF, 2));
        enchanter.addItem(new ItemStack(Material.ENCHANTMENT_TABLE, 1));
        enchanter.addItem(new ItemStack(Material.BOOKSHELF, 10));
        enchanter.addItem(new ItemStack(Material.EXP_BOTTLE, 16));
        Dye dye = new Dye();
        dye.setColor(DyeColor.BLUE);
        ItemStack enchanter_1 = dye.toItemStack();
        enchanter_1.setAmount(16);
        enchanter.addItem(enchanter_1);
        enchanter.addItem(trackingCompass.clone());
        ItemStack enchanter_2 = new ItemStack(Material.LEATHER_HELMET);
        enchanter_2.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        LeatherArmorMeta enchanter_2_meta = (LeatherArmorMeta) enchanter_2.getItemMeta();
        enchanter_2_meta.setColor(Color.GREEN);
        enchanter_2.setItemMeta(enchanter_2_meta);
        enchanter.addArmor(enchanter_2, new ItemStack(Material.GOLD_CHESTPLATE),
                new ItemStack(Material.GOLD_LEGGINGS), new ItemStack(Material.GOLD_BOOTS));
        this.kitList.add(enchanter);

        Kit sonic = new Kit(Kit.KitID.SONIC, "Sonic", "§7", new ItemStack(Material.SUGAR));
        ItemStack sonic_1 = new ItemStack(Material.WOOD_SWORD);
        sonic_1.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
        sonic_1.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
        sonic.addItem(sonic_1);
        sonic.addItem(new ItemStack(Material.APPLE, 16));
        sonic.addItem(trackingCompass.clone());
        ItemStack sonic_2 = new ItemStack(Material.LEATHER_HELMET);
        sonic_2.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        LeatherArmorMeta sonic_2_meta = (LeatherArmorMeta) sonic_2.getItemMeta();
        sonic_2_meta.setColor(Color.BLUE);
        sonic_2.setItemMeta(sonic_2_meta);
        ItemStack sonic_3 = new ItemStack(Material.LEATHER_BOOTS);
        sonic_3.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        sonic_3.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 4);
        LeatherArmorMeta sonic_3_meta = (LeatherArmorMeta) sonic_3.getItemMeta();
        sonic_3_meta.setColor(Color.BLUE);
        sonic_3.setItemMeta(sonic_3_meta);
        sonic.addArmor(sonic_2, new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                new ItemStack(Material.CHAINMAIL_LEGGINGS), sonic_3);
        sonic.getEffects().add(PotionEffectType.SPEED);
        this.kitList.add(sonic);

        Kit hermes = new Kit(Kit.KitID.HERMES, "Hermes", "§7", new ItemStack(Material.RABBIT_FOOT));
        hermes.addItem(new ItemStack(Material.STONE_SWORD, 1));
        hermes.addItem(new ItemStack(Material.BREAD, 6));
        hermes.addItem(new ItemStack(Material.ENDER_PEARL, 2));
        ItemStack hermes_1 = new ItemStack(Material.PAPER);
        hermes_1.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta hermes_1_meta = hermes_1.getItemMeta();
        hermes_1_meta.setDisplayName("§dSalto");
        hermes_1_meta.setLore(new ArrayList<>(Collections.singletonList("§d[ Kit HERMES ]")));
        hermes_1_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        hermes_1.setItemMeta(hermes_1_meta);
        hermes.addItem(hermes_1);
        hermes.addItem(trackingCompass.clone());
        ItemStack hermes_2 = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta hermes_2_meta = (LeatherArmorMeta) hermes_2.getItemMeta();
        hermes_2_meta.setColor(Color.YELLOW);
        hermes_2.setItemMeta(hermes_2_meta);
        ItemStack hermes_3 = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta hermes_3_meta = (LeatherArmorMeta) hermes_3.getItemMeta();
        hermes_3_meta.setColor(Color.YELLOW);
        hermes_3.setItemMeta(hermes_3_meta);
        ItemStack hermes_4 = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta hermes_4_meta = (LeatherArmorMeta) hermes_4.getItemMeta();
        hermes_4_meta.setColor(Color.YELLOW);
        hermes_4.setItemMeta(hermes_4_meta);
        ItemStack hermes_5 = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta hermes_5_meta = (LeatherArmorMeta) hermes_5.getItemMeta();
        hermes_5_meta.setColor(Color.YELLOW);
        hermes_5.setItemMeta(hermes_5_meta);
        hermes.addArmor(hermes_2, hermes_3, hermes_4, hermes_5);
        this.kitList.add(hermes);

        Kit chemist = new Kit(Kit.KitID.CHEMIST, "Chemist", "§7", new ItemStack(Material.POTION));
        ItemStack chemist_1 = new ItemStack(Material.POTION, 2);
        Potion chemist_1_pot = new Potion(1);
        chemist_1_pot.setType(PotionType.WEAKNESS);
        chemist_1_pot.setHasExtendedDuration(false);
        chemist_1_pot.setSplash(true);
        chemist_1_pot.apply(chemist_1);
        chemist.addItem(chemist_1);
        ItemStack chemist_2 = new ItemStack(Material.POTION, 2);
        Potion chemist_2_pot = new Potion(1);
        chemist_2_pot.setType(PotionType.POISON);
        chemist_2_pot.setHasExtendedDuration(false);
        chemist_2_pot.setSplash(true);
        chemist_2_pot.apply(chemist_2);
        chemist.addItem(chemist_2);
        ItemStack chemist_3 = new ItemStack(Material.POTION, 2);
        Potion chemist_3_pot = new Potion(1);
        chemist_3_pot.setType(PotionType.SLOWNESS);
        chemist_3_pot.setHasExtendedDuration(false);
        chemist_3_pot.setSplash(true);
        chemist_3_pot.apply(chemist_3);
        chemist.addItem(chemist_3);
        ItemStack chemist_4 = new ItemStack(Material.POTION, 2);
        Potion chemist_4_pot = new Potion(1);
        chemist_4_pot.setType(PotionType.INSTANT_DAMAGE);
        chemist_4_pot.setLevel(2);
        chemist_4_pot.setSplash(true);
        chemist_4_pot.apply(chemist_4);
        chemist.addItem(chemist_4);
        ItemStack chemist_5 = new ItemStack(Material.POTION, 2);
        Potion chemist_5_pot = new Potion(1);
        chemist_5_pot.setType(PotionType.SPEED);
        chemist_5_pot.setLevel(2);
        chemist_5_pot.setSplash(true);
        chemist_5_pot.apply(chemist_5);
        chemist.addItem(chemist_5);
        ItemStack chemist_6 = new ItemStack(Material.POTION, 2);
        Potion chemist_6_pot = new Potion(1);
        chemist_6_pot.setType(PotionType.INSTANT_HEAL);
        chemist_6_pot.setSplash(true);
        chemist_6_pot.apply(chemist_6);
        chemist.addItem(chemist_6);
        ItemStack chemist_7 = new ItemStack(Material.POTION, 2);
        Potion chemist_7_pot = new Potion(1);
        chemist_7_pot.setType(PotionType.REGEN);
        chemist_7_pot.setSplash(true);
        chemist_7_pot.apply(chemist_7);
        chemist.addItem(chemist_7);
        ItemStack chemist_8 = new ItemStack(Material.PAPER);
        chemist_8.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta chemist_8_meta = chemist_8.getItemMeta();
        chemist_8_meta.setDisplayName("§dPoción");
        chemist_8_meta.setLore(new ArrayList<>(Collections.singletonList("§d[ Kit CHEMIST ]")));
        chemist_8_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        chemist_8.setItemMeta(chemist_8_meta);
        chemist.addItem(chemist_8);
        chemist.addItem(trackingCompass.clone());
        chemist.addArmor(new ItemStack(Material.CHAINMAIL_HELMET), new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.CHAINMAIL_BOOTS));
        this.kitList.add(chemist);

        Kit pyro = new Kit(Kit.KitID.PYRO, "Pyro", "§7", new ItemStack(Material.BLAZE_POWDER));
        pyro.addItem(new ItemStack(Material.FLINT_AND_STEEL, 1));
        pyro.addItem(new ItemStack(Material.APPLE, 5));
        pyro.addItem(trackingCompass.clone());
        pyro.addArmor(null, null, null, null);
        this.kitList.add(pyro);

        Kit warrior = new Kit(Kit.KitID.WARRIOR, "Warrior", "§7", new ItemStack(Material.STONE_SWORD));
        warrior.addItem(new ItemStack(Material.IRON_SWORD, 1));
        warrior.addItem(new ItemStack(Material.COOKED_BEEF, 5));
        warrior.addItem(trackingCompass.clone());
        ItemStack warrior_1 = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta warrior_1_meta = (LeatherArmorMeta) warrior_1.getItemMeta();
        warrior_1_meta.setColor(Color.BLACK);
        warrior_1.setItemMeta(warrior_1_meta);
        ItemStack warrior_2 = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta warrior_2_meta = (LeatherArmorMeta) warrior_2.getItemMeta();
        warrior_2_meta.setColor(Color.BLACK);
        warrior_2.setItemMeta(warrior_2_meta);
        warrior.addArmor(new ItemStack(Material.GOLD_HELMET), new ItemStack(Material.GOLD_CHESTPLATE), warrior_1, warrior_2);
        warrior.getEffects().add(PotionEffectType.SLOW);
        warrior.getEffects().add(PotionEffectType.SLOW_DIGGING);
        this.kitList.add(warrior);

        Kit treasure_hunter = new Kit(Kit.KitID.TREASURE_HUNTER, "Treasure Hunter", "§7", new ItemStack(Material.DIAMOND_PICKAXE));
        treasure_hunter.addItem(new ItemStack(Material.GOLD_SWORD, 1));
        treasure_hunter.addItem(new ItemStack(Material.DIAMOND_PICKAXE, 1));
        treasure_hunter.addItem(new ItemStack(Material.DIAMOND_SPADE, 1));
        treasure_hunter.addItem(new ItemStack(Material.BREAD, 5));
        treasure_hunter.addItem(new ItemStack(Material.COOKED_BEEF, 2));
        treasure_hunter.addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
        treasure_hunter.addItem(trackingCompass.clone());
        treasure_hunter.addArmor(new ItemStack(Material.GOLD_HELMET), null, null, null);
        treasure_hunter.getEffects().add(PotionEffectType.FAST_DIGGING);
        treasure_hunter.getEffects().add(PotionEffectType.NIGHT_VISION);
        this.kitList.add(treasure_hunter);

        Kit transporter = new Kit(Kit.KitID.TRANSPORTER, "Transporter", "§7", new ItemStack(Material.ENDER_PEARL));
        ItemStack transporter_1 = new ItemStack(Material.WOOD_SWORD);
        transporter_1.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
        transporter.addItem(transporter_1);
        transporter.addItem(new ItemStack(Material.ENDER_PEARL, 10));
        ItemStack transporter_2 = new ItemStack(Material.POTION, 3);
        Potion transporter_2_pot = new Potion(1);
        transporter_2_pot.setType(PotionType.INSTANT_HEAL);
        transporter_2_pot.setSplash(false);
        transporter_2_pot.apply(transporter_2);
        transporter.addItem(transporter_2);
        ItemStack transporter_6 = new ItemStack(Material.PAPER);
        transporter_6.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta transporter_6_meta = transporter_6.getItemMeta();
        transporter_6_meta.setDisplayName("§dTP random");
        transporter_6_meta.setLore(new ArrayList<>(Collections.singletonList("§d[ Kit TRANSPORTER ]")));
        transporter_6_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        transporter_6.setItemMeta(transporter_6_meta);
        transporter.addItem(transporter_6);
        transporter.addItem(trackingCompass.clone());
        ItemStack transporter_3 = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta transporter_3_meta = (LeatherArmorMeta) transporter_3.getItemMeta();
        transporter_3_meta.setColor(Color.WHITE);
        transporter_3.setItemMeta(transporter_3_meta);
        ItemStack transporter_4 = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta transporter_4_meta = (LeatherArmorMeta) transporter_4.getItemMeta();
        transporter_4_meta.setColor(Color.WHITE);
        transporter_4.setItemMeta(transporter_4_meta);
        ItemStack transporter_5 = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta transporter_5_meta = (LeatherArmorMeta) transporter_5.getItemMeta();
        transporter_5_meta.setColor(Color.WHITE);
        transporter_5.setItemMeta(transporter_5_meta);
        transporter.addArmor(transporter_3, transporter_4, new ItemStack(Material.IRON_LEGGINGS), transporter_5);
        this.kitList.add(transporter);

        /*Kit hunter = new Kit(Kit.KitID.HUNTER, "Hunter", "§7", new ItemStack(Material.COOKED_BEEF));
        ItemStack hunter_1 = new ItemStack(Material.STONE_SWORD);
        hunter_1.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
        hunter.addItem(hunter_1);
        hunter.addItem(new ItemStack(Material.COOKED_BEEF, 2));
        hunter.addItem(trackingCompass.clone());
        ItemStack hunter_2 = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta hunter_2_meta = (LeatherArmorMeta) hunter_2.getItemMeta();
        hunter_2_meta.setColor(Color.OLIVE);
        hunter_2.setItemMeta(hunter_2_meta);
        ItemStack hunter_3 = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta hunter_3_meta = (LeatherArmorMeta) hunter_3.getItemMeta();
        hunter_3_meta.setColor(Color.OLIVE);
        hunter_3.setItemMeta(hunter_3_meta);
        hunter.addArmor(new ItemStack(Material.CHAINMAIL_HELMET), hunter_2, hunter_3, new ItemStack(Material.CHAINMAIL_BOOTS));
        hunter.getEffects().add(PotionEffectType.INCREASE_DAMAGE);
        hunter.getEffects().add(PotionEffectType.SLOW);
        this.kitList.add(hunter);*/

        Kit hawkeye = new Kit(Kit.KitID.HAWKEYE, "Hawkeye", "§7", new ItemStack(Material.BOW));
        hawkeye.addItem(new ItemStack(Material.BOW, 1));
        hawkeye.addItem(new ItemStack(Material.COOKED_BEEF, 2));
        ItemStack hawkeye_3 = new ItemStack(Material.PAPER);
        hawkeye_3.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta hawkeye_3_meta = hawkeye_3.getItemMeta();
        hawkeye_3_meta.setDisplayName("§dInvisible");
        hawkeye_3_meta.setLore(new ArrayList<>(Collections.singletonList("§d[ Kit HAWKEYE ]")));
        hawkeye_3_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        hawkeye_3.setItemMeta(hawkeye_3_meta);
        hawkeye.addItem(hawkeye_3);
        hawkeye.addItem(new ItemStack(Material.ARROW, 16));
        hawkeye.addItem(trackingCompass.clone());
        ItemStack hawkeye_1 = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta hawkeye_1_meta = (LeatherArmorMeta) hawkeye_1.getItemMeta();
        hawkeye_1_meta.setColor(Color.AQUA);
        hawkeye_1.setItemMeta(hawkeye_1_meta);
        ItemStack hawkeye_2 = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta hawkeye_2_meta = (LeatherArmorMeta) hawkeye_2.getItemMeta();
        hawkeye_2_meta.setColor(Color.AQUA);
        hawkeye_2.setItemMeta(hawkeye_2_meta);
        hawkeye.addArmor(hawkeye_1, new ItemStack(Material.GOLD_CHESTPLATE),
                new ItemStack(Material.GOLD_LEGGINGS), hawkeye_2);
        hawkeye.getEffects().add(PotionEffectType.SPEED);
        this.kitList.add(hawkeye);

        Kit katniss = new Kit(Kit.KitID.KATNISS, "Katniss", "§7", new ItemStack(Material.GOLDEN_APPLE));
        ItemStack katniss_1 = new ItemStack(Material.BOW);
        katniss_1.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
        katniss.addItem(katniss_1);
        katniss.addItem(new ItemStack(Material.COOKED_BEEF, 2));
        katniss.addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
        ItemStack katniss_4 = new ItemStack(Material.PAPER);
        katniss_4.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta katniss_4_meta = katniss_4.getItemMeta();
        katniss_4_meta.setDisplayName("§dFlechas ígneas");
        katniss_4_meta.setLore(new ArrayList<>(Collections.singletonList("§d[ Kit KATNISS ]")));
        katniss_4_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        katniss_4.setItemMeta(katniss_4_meta);
        katniss.addItem(katniss_4);
        katniss.addItem(new ItemStack(Material.ARROW, 12));
        katniss.addItem(trackingCompass.clone());
        ItemStack katniss_2 = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta katniss_2_meta = (LeatherArmorMeta) katniss_2.getItemMeta();
        katniss_2_meta.setColor(Color.RED);
        katniss_2.setItemMeta(katniss_2_meta);
        ItemStack katniss_3 = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta katniss_3_meta = (LeatherArmorMeta) katniss_3.getItemMeta();
        katniss_3_meta.setColor(Color.RED);
        katniss_3.setItemMeta(katniss_3_meta);
        katniss.addArmor(katniss_2, new ItemStack(Material.GOLD_CHESTPLATE),
                new ItemStack(Material.GOLD_LEGGINGS), katniss_3);
        katniss.getEffects().add(PotionEffectType.NIGHT_VISION);
        this.kitList.add(katniss);
    }

    public void fillInventory() {
        this.kitList.forEach(kit -> {
            ItemStack itemStack = kit.getIcon().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(kit.getColor() + kit.getName());
            itemStack.setItemMeta(itemMeta);
            this.inventory.addItem(itemStack);
        });
    }

}
