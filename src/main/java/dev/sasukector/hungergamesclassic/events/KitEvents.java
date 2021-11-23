package dev.sasukector.hungergamesclassic.events;

import dev.sasukector.hungergamesclassic.controllers.GameController;
import dev.sasukector.hungergamesclassic.controllers.KitController;
import dev.sasukector.hungergamesclassic.helpers.ServerUtilities;
import dev.sasukector.hungergamesclassic.models.Kit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class KitEvents implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (GameController.getInstance().getAlivePlayers().contains(player.getUniqueId())) {
                Kit playerKit = KitController.getInstance().getPlayersKits().get(player.getUniqueId());
                if (playerKit == null) return;
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    if (playerKit.getId() == Kit.KitID.HERMES) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (GameController.getInstance().getCurrentStatus() == GameController.Status.PLAYING) {
            Player player = event.getPlayer();
            if(event.getAction() == Action.RIGHT_CLICK_AIR) {
                ItemStack itemStack = player.getItemInHand();
                if (itemStack != null && itemStack.hasItemMeta()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (itemStack.getType() == Material.PAPER && itemMeta.getDisplayName().equals("§dSalto")) {
                        Kit playerKit = KitController.getInstance().getPlayersKits().get(player.getUniqueId());
                        if (playerKit == null) return;
                        if (playerKit.getId() == Kit.KitID.HERMES) {
                            if (KitController.getInstance().getPlayersTimers().get(player.getUniqueId()) != null) {
                                player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1, 2);
                                ServerUtilities.sendActionBar(player, "§4Usable en §c" +
                                        KitController.getInstance().getPlayersTimers().get(player.getUniqueId()) +
                                        " s");
                            } else {
                                player.playSound(player.getLocation(), Sound.HORSE_JUMP, 1, 2);
                                player.setVelocity(new Vector(0, 2, 0));
                                KitController.getInstance().getPlayersTimers().put(player.getUniqueId(), 15);
                            }
                        } else {
                            player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1, 2);
                            ServerUtilities.sendActionBar(player, "§4No eres kit §cHERMES");
                        }
                    }
                }
            }
        }
    }

}
