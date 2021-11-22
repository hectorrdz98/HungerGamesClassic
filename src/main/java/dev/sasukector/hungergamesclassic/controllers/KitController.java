package dev.sasukector.hungergamesclassic.controllers;

import dev.sasukector.hungergamesclassic.helpers.ServerUtilities;
import dev.sasukector.hungergamesclassic.models.Kit;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KitController {

    private static KitController instance = null;
    private final @Getter List<Kit> kitList;
    private final @Getter Map<UUID, Kit> playersKits;
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
        this.inventory = Bukkit.createInventory(null, 18, "§dSeleccionar Kit");
        this.createKits();
        this.fillInventory();
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
        enchanter.addItem(new ItemStack(Material.ENCHANTMENT_TABLE, 1));
        enchanter.addItem(new ItemStack(Material.STONE_SWORD, 1));
        enchanter.addItem(trackingCompass.clone());
        this.kitList.add(enchanter);

        Kit cupid = new Kit(Kit.KitID.CUPID, "Cupid", "§7", new ItemStack(Material.RABBIT_FOOT));
        cupid.addItem(new ItemStack(Material.COOKED_BEEF, 2));
        ItemStack cupid_1 = new ItemStack(Material.PAPER);
        cupid_1.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta cupid_1_meta = cupid_1.getItemMeta();
        cupid_1_meta.setDisplayName("§dSalto");
        cupid_1_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        cupid_1.setItemMeta(cupid_1_meta);
        cupid.addItem(cupid_1);
        cupid.addItem(trackingCompass.clone());
        this.kitList.add(cupid);
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
