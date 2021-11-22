package dev.sasukector.hungergamesclassic.events;

import dev.sasukector.hungergamesclassic.controllers.GameController;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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

}
