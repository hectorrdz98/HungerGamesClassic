package dev.sasukector.hungergamesclassic.events;

import dev.sasukector.hungergamesclassic.controllers.ArenaController;
import dev.sasukector.hungergamesclassic.controllers.GameController;
import dev.sasukector.hungergamesclassic.controllers.KitController;
import dev.sasukector.hungergamesclassic.helpers.ServerUtilities;
import dev.sasukector.hungergamesclassic.models.Arena;
import dev.sasukector.hungergamesclassic.models.Kit;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

public class KitEvents implements Listener {

    private final Random random = new Random();

    public boolean usableAgain(Player player) {
        if (KitController.getInstance().getPlayersTimers().get(player.getUniqueId()) != null) {
            player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1, 2);
            ServerUtilities.sendActionBar(player, "§4Usable en §c" +
                    KitController.getInstance().getPlayersTimers().get(player.getUniqueId()) +
                    " s");
            return false;
        }
        return true;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (GameController.getInstance().getAlivePlayers().contains(player.getUniqueId())) {
                Kit playerKit = KitController.getInstance().getPlayersKits().get(player.getUniqueId());
                if (playerKit == null) return;
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    if (playerKit.getId() == Kit.KitID.HERMES || playerKit.getId() == Kit.KitID.TRANSPORTER) {
                        event.setCancelled(true);
                    }
                } else if (event.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                        event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                    if (playerKit.getId() == Kit.KitID.PYRO) {
                        event.setCancelled(true);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 3, 0, false, false));
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
                    Kit playerKit = KitController.getInstance().getPlayersKits().get(player.getUniqueId());
                    if (playerKit == null) return;
                    if (itemStack.getType() == Material.PAPER && itemMeta.getDisplayName().equals("§dSalto")) {
                        if (playerKit.getId() == Kit.KitID.HERMES) {
                            if (this.usableAgain(player)) {
                                player.playSound(player.getLocation(), Sound.HORSE_JUMP, 1, 2);
                                player.setVelocity(new Vector(0, 2, 0));
                                KitController.getInstance().getPlayersTimers().put(player.getUniqueId(), 15);
                            }
                        } else {
                            player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1, 2);
                            ServerUtilities.sendActionBar(player, "§4No eres kit §cHERMES");
                        }
                    } else if (itemStack.getType() == Material.PAPER && itemMeta.getDisplayName().equals("§dPoción")) {
                        if (playerKit.getId() == Kit.KitID.CHEMIST) {
                            if (this.usableAgain(player)) {
                                player.playSound(player.getLocation(), Sound.SPLASH2, 1, 2);
                                ItemStack potion = new ItemStack(Material.POTION, 1);
                                Potion potion_pot = new Potion(1);
                                potion_pot.setType(ArenaController.getInstance().getRandomPotionType());
                                potion_pot.setLevel(2);
                                potion_pot.setSplash(true);
                                potion_pot.apply(potion);
                                player.getInventory().addItem(potion);
                                KitController.getInstance().getPlayersTimers().put(player.getUniqueId(), 30);
                            }
                        } else {
                            player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1, 2);
                            ServerUtilities.sendActionBar(player, "§4No eres kit §cCHEMIST");
                        }
                    } else if (itemStack.getType() == Material.PAPER && itemMeta.getDisplayName().equals("§dTP random")) {
                        if (playerKit.getId() == Kit.KitID.TRANSPORTER) {
                            if (this.usableAgain(player)) {
                                player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 2);
                                Arena arena = GameController.getInstance().getCurrentArena();
                                Location safeLocation = null;
                                while (safeLocation == null) {
                                    int x = random.nextInt(2 * arena.getMaxRadius()) - arena.getMaxRadius();
                                    int z = random.nextInt(2 * arena.getMaxRadius()) - arena.getMaxRadius();
                                    safeLocation = ServerUtilities.getSafeLocation(new Location(arena.getWorld(), x, 0, z));
                                }
                                player.teleport(safeLocation);
                                KitController.getInstance().getPlayersTimers().put(player.getUniqueId(), 30);
                            }
                        } else {
                            player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1, 2);
                            ServerUtilities.sendActionBar(player, "§4No eres kit §cTRANSPORTER");
                        }
                    } else if (itemStack.getType() == Material.PAPER && itemMeta.getDisplayName().equals("§dInvisible")) {
                        if (playerKit.getId() == Kit.KitID.HAWKEYE) {
                            if (this.usableAgain(player)) {
                                player.playSound(player.getLocation(), Sound.SILVERFISH_KILL, 1, 2);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 5, 0, false, false));
                                KitController.getInstance().getPlayersTimers().put(player.getUniqueId(), 15);
                            }
                        } else {
                            player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1, 2);
                            ServerUtilities.sendActionBar(player, "§4No eres kit §cHAWKEYE");
                        }
                    } else if (itemStack.getType() == Material.PAPER && itemMeta.getDisplayName().equals("§dFlechas ígneas")) {
                        if (playerKit.getId() == Kit.KitID.KATNISS) {
                            if (this.usableAgain(player)) {
                                player.playSound(player.getLocation(), Sound.BLAZE_BREATH, 1, 2);
                                KitController.getInstance().getPlayersTimers().put(player.getUniqueId(), 15);
                            }
                        } else {
                            player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1, 2);
                            ServerUtilities.sendActionBar(player, "§4No eres kit §cHAWKEYE");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (GameController.getInstance().getCurrentStatus() == GameController.Status.PLAYING) {
            if (event.getEntity() instanceof Player) {
                Player shooter = (Player) event.getEntity();
                if (shooter != null && GameController.getInstance().getAlivePlayers().contains(shooter.getUniqueId())) {
                    Kit playerKit = KitController.getInstance().getPlayersKits().get(shooter.getUniqueId());
                    if (playerKit == null) return;
                    if (KitController.getInstance().getPlayersTimers().get(shooter.getUniqueId()) != null) {
                        shooter.playSound(shooter.getLocation(), Sound.BLAZE_BREATH, 1, 2);
                        event.getProjectile().setFireTicks(9999);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (GameController.getInstance().getCurrentStatus() == GameController.Status.PLAYING) {
            Player killer = event.getEntity().getKiller();
            if (killer != null && GameController.getInstance().getAlivePlayers().contains(killer.getUniqueId())) {
                Kit playerKit = KitController.getInstance().getPlayersKits().get(killer.getUniqueId());
                if (playerKit == null) return;
                switch (playerKit.getId()) {
                    case ENCHANTER: {
                        killer.getInventory().addItem(new ItemStack(Material.EXP_BOTTLE, random.nextInt(4) + 1));
                        Dye dye = new Dye();
                        dye.setColor(DyeColor.BLUE);
                        ItemStack lapis = dye.toItemStack();
                        lapis.setAmount(random.nextInt(4) + 1);
                        killer.getInventory().addItem(lapis);
                    } break;
                    case SONIC: {
                        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 3, false, false));
                        killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 2, 0, false, false));
                    } break;
                    case HERMES:
                    case TRANSPORTER: {
                        killer.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 2));
                        killer.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 2));
                    } break;
                    case CHEMIST:
                    case PYRO:
                    case WARRIOR:
                    case TREASURE_HUNTER:
                    case HUNTER: {
                        killer.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 2));
                    } break;
                    case HAWKEYE:
                    case KATNISS: {
                        killer.getInventory().addItem(new ItemStack(Material.ARROW, random.nextInt(6) + 1));
                        killer.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 1));
                    } break;
                }
            }
        }
    }

}
