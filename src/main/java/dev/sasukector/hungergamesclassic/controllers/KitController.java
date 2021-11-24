package dev.sasukector.hungergamesclassic.controllers;

import dev.sasukector.hungergamesclassic.HungerGamesClassic;
import dev.sasukector.hungergamesclassic.helpers.ServerUtilities;
import dev.sasukector.hungergamesclassic.models.Kit;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
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
            List<UUID> timersUUIDs = new ArrayList<>(playersTimers.keySet());
            for (UUID timerUUID : timersUUIDs) {
                if (Bukkit.getPlayer(timerUUID) == null) {
                    playersTimers.remove(timerUUID);
                } else {
                    int newTimer = playersTimers.get(timerUUID) - 1;
                    playersTimers.put(timerUUID, newTimer);
                    if (newTimer <= 0) {
                        playersTimers.remove(timerUUID);
                    }
                }
            }
        }, 0L, 20L);
    }

    private void createKits() {
        ItemStack trackingCompass = new ItemStack(Material.COMPASS);
        trackingCompass.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta trackingCompassMeta = trackingCompass.getItemMeta();
        trackingCompassMeta.setDisplayName("§dJugador cercano");
        trackingCompassMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        trackingCompass.setItemMeta(trackingCompassMeta);

        Kit enchanter = new Kit(Kit.KitID.ENCHANTER, "Enchanter", "§7", new ItemStack(Material.ENCHANTMENT_TABLE));
        enchanter.addItem(new ItemStack(Material.COOKED_BEEF, 2));
        enchanter.addItem(new ItemStack(Material.BOOKSHELF, 10));
        enchanter.addItem(new ItemStack(Material.EXP_BOTTLE, 16));
        Dye dye = new Dye();
        dye.setColor(DyeColor.BLUE);
        ItemStack enchanter_1 = dye.toItemStack();
        enchanter_1.setAmount(16);
        enchanter.addItem(enchanter_1);
        enchanter.addItem(new ItemStack(Material.ENCHANTMENT_TABLE, 1));
        enchanter.addItem(new ItemStack(Material.STONE_SWORD, 1));
        enchanter.addItem(trackingCompass.clone());
        enchanter.addArmor(null, new ItemStack(Material.LEATHER_CHESTPLATE), null, null);
        this.kitList.add(enchanter);

        Kit sonic = new Kit(Kit.KitID.SONIC, "Sonic", "§7", new ItemStack(Material.SUGAR));
        sonic.addItem(new ItemStack(Material.WOOD_SWORD, 1));
        sonic.addItem(new ItemStack(Material.APPLE, 2));
        sonic.addItem(trackingCompass.clone());
        sonic.addArmor(null, new ItemStack(Material.CHAINMAIL_CHESTPLATE), null, new ItemStack(Material.LEATHER_BOOTS));
        sonic.getEffects().add(PotionEffectType.SPEED);
        this.kitList.add(sonic);

        Kit hermes = new Kit(Kit.KitID.HERMES, "Hermes", "§7", new ItemStack(Material.RABBIT_FOOT));
        hermes.addItem(new ItemStack(Material.BREAD, 3));
        hermes.addItem(new ItemStack(Material.WOOD_SWORD, 1));
        ItemStack hermes_1 = new ItemStack(Material.PAPER);
        hermes_1.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta hermes_1_meta = hermes_1.getItemMeta();
        hermes_1_meta.setDisplayName("§dSalto");
        hermes_1_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        hermes_1.setItemMeta(hermes_1_meta);
        hermes.addItem(hermes_1);
        hermes.addItem(trackingCompass.clone());
        hermes.addArmor(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE),
                null, new ItemStack(Material.LEATHER_BOOTS));
        this.kitList.add(hermes);

        Kit chemist = new Kit(Kit.KitID.CHEMIST, "Chemist", "§7", new ItemStack(Material.POTION));
        ItemStack chemist_1 = new ItemStack(Material.POTION, 2);
        Potion chemist_1_pot = new Potion(1);
        chemist_1_pot.setType(PotionType.WEAKNESS);
        chemist_1_pot.setHasExtendedDuration(false);
        chemist_1_pot.setSplash(true);
        chemist_1_pot.apply(chemist_1);
        chemist.addItem(chemist_1);
        ItemStack chemist_2 = new ItemStack(Material.POTION, 1);
        Potion chemist_2_pot = new Potion(1);
        chemist_2_pot.setType(PotionType.POISON);
        chemist_2_pot.setHasExtendedDuration(false);
        chemist_2_pot.setSplash(true);
        chemist_2_pot.apply(chemist_2);
        chemist.addItem(chemist_2);
        ItemStack chemist_3 = new ItemStack(Material.POTION, 1);
        Potion chemist_3_pot = new Potion(1);
        chemist_3_pot.setType(PotionType.SLOWNESS);
        chemist_3_pot.setHasExtendedDuration(false);
        chemist_3_pot.setSplash(true);
        chemist_3_pot.apply(chemist_3);
        chemist.addItem(chemist_3);
        ItemStack chemist_4 = new ItemStack(Material.POTION, 1);
        Potion chemist_4_pot = new Potion(1);
        chemist_4_pot.setType(PotionType.INSTANT_DAMAGE);
        chemist_4_pot.setLevel(2);
        chemist_4_pot.setSplash(true);
        chemist_4_pot.apply(chemist_4);
        chemist.addItem(chemist_4);
        chemist.addItem(trackingCompass.clone());
        chemist.addArmor(null, null, null, null);
        this.kitList.add(chemist);

        Kit pyro = new Kit(Kit.KitID.PYRO, "Pyro", "§7", new ItemStack(Material.BLAZE_POWDER));
        pyro.addItem(new ItemStack(Material.FLINT_AND_STEEL, 1));
        pyro.addItem(new ItemStack(Material.APPLE, 3));
        pyro.addItem(trackingCompass.clone());
        pyro.addArmor(null, null, null, null);
        this.kitList.add(pyro);

        Kit warrior = new Kit(Kit.KitID.WARRIOR, "Warrior", "§7", new ItemStack(Material.STONE_SWORD));
        warrior.addItem(new ItemStack(Material.STONE_SWORD, 1));
        warrior.addItem(new ItemStack(Material.COOKED_BEEF, 2));
        warrior.addItem(trackingCompass.clone());
        warrior.addArmor(null, null, new ItemStack(Material.LEATHER_LEGGINGS), null);
        this.kitList.add(pyro);
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
