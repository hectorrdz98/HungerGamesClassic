package dev.sasukector.hungergamesclassic.events;

import dev.sasukector.hungergamesclassic.controllers.GameController;
import dev.sasukector.hungergamesclassic.controllers.KitController;
import dev.sasukector.hungergamesclassic.helpers.ServerUtilities;
import dev.sasukector.hungergamesclassic.models.Kit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GameEvents implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (GameController.getInstance().getCurrentStatus() == GameController.Status.PLAYING) {
            Player player = event.getEntity();
            if (GameController.getInstance().getAlivePlayers().contains(player.getUniqueId())) {
                GameController.getInstance().getAlivePlayers().remove(player.getUniqueId());
                player.getWorld().playEffect(player.getLocation(), Effect.HEART, 128);
                player.getWorld().playSound(player.getLocation(), Sound.BAT_DEATH, 1, 1);
                player.spigot().respawn();
                GameController.getInstance().validateWin();
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (GameController.getInstance().getCurrentStatus() == GameController.Status.PLAYING) {
            Player player = event.getPlayer();
            if (!GameController.getInstance().getAlivePlayers().contains(player.getUniqueId())) {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (GameController.getInstance().getCurrentStatus() == GameController.Status.FROZEN) {
            Player player = event.getPlayer();
            if(event.getAction() == Action.RIGHT_CLICK_AIR) {
                ItemStack itemStack = player.getItemInHand();
                if (itemStack != null && itemStack.hasItemMeta()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (itemStack.getType() == Material.BOOK && itemMeta.getDisplayName().equals("§dSeleccionar Kit")) {
                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
                        player.openInventory(KitController.getInstance().getInventory());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
            ItemStack itemStack = event.getCurrentItem();
            Inventory inventory = event.getClickedInventory();
            if (itemStack != null && itemStack.hasItemMeta() &&
                    inventory != null && inventory.equals(KitController.getInstance().getInventory())) {
                if (GameController.getInstance().getCurrentStatus() == GameController.Status.FROZEN) {
                    Kit kit = KitController.getInstance().getKit(itemStack.getItemMeta().getDisplayName());
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
                    if (kit != null) {
                        KitController.getInstance().getPlayersKits().put(player.getUniqueId(), kit);
                        ServerUtilities.sendServerMessage(player, "Has elegido el kit " +
                                kit.getColor() + "§l" + kit.getName());
                    } else {
                        ServerUtilities.sendServerMessage(player, "§cNo se pudo seleccionar el kit");
                    }
                }
            }
        }
    }

}
