package dev.sasukector.hungergamesclassic.events;

import dev.sasukector.hungergamesclassic.controllers.GameController;
import dev.sasukector.hungergamesclassic.helpers.ServerUtilities;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(ChatColor.GREEN + "+ " + player.getName());
        GameController.getInstance().handlePlayerJoin(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(ChatColor.RED + "- " + player.getName());
        GameController.getInstance().handlePlayerLeave(player);
    }

    @EventHandler
    public void onMobSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            event.setCancelled(true);
        } else if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if (!GameController.getInstance().isPvpEnabled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockChestInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof InventoryHolder) {
                if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                        event.setCancelled(true);
                } else {
                    if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                        event.setCancelled(true);
                    }
                }
            }
        } else if (GameController.getInstance().getCurrentStatus() == GameController.Status.LOBBY) {
            Player player = event.getPlayer();
            if(event.getAction() == Action.RIGHT_CLICK_AIR) {
                ItemStack itemStack = player.getItemInHand();
                if (itemStack != null && itemStack.hasItemMeta()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (itemStack.getType() == Material.STONE_SWORD && itemMeta.getDisplayName().equals("§dUnirse a cola")) {
                        if (!GameController.getInstance().getAlivePlayers().contains(player.getUniqueId())) {
                            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
                            GameController.getInstance().getAlivePlayers().add(player.getUniqueId());
                            int currentQueuePlayers = GameController.getInstance().getAlivePlayers().size();
                            ServerUtilities.sendBroadcastMessage("§d§l" + player.getName() +
                                    "§r§5 se unió a la cola [ §e§l" + currentQueuePlayers + " §r§5]");
                            GameController.getInstance().checkIfGamePossible();
                            player.getInventory().clear();
                            player.getEquipment().setArmorContents(new ItemStack[]{ null, null, null, null });
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDroppedItem(PlayerDropItemEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemEaten(PlayerItemConsumeEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickedUpItems(PlayerPickupItemEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

}
