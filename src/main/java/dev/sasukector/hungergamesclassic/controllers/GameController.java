package dev.sasukector.hungergamesclassic.controllers;

import dev.sasukector.hungergamesclassic.HungerGamesClassic;
import dev.sasukector.hungergamesclassic.helpers.ServerUtilities;
import dev.sasukector.hungergamesclassic.models.Arena;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class GameController {

    private static GameController instance = null;
    private @Getter Status currentStatus = Status.LOBBY;
    private final @Getter List<UUID> alivePlayers;
    private @Getter Arena currentArena = null;
    private @Getter boolean pvpEnabled = false;
    private int pvpEnabledTaskID = -1;

    public enum Status {
        LOBBY, WAITING, FROZEN, PLAYING
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public GameController() {
        this.alivePlayers = new ArrayList<>();
    }

    public void handlePlayerJoin(Player player) {
        this.restartPlayer(player);
        this.teleportPlayerToLobby(player);
        if (this.currentStatus != Status.LOBBY) {
            player.setGameMode(GameMode.SPECTATOR);
            this.currentArena.teleportPlayer(player);
        }
    }

    public void handlePlayerLeave(Player player) {
        if (this.alivePlayers.contains(player.getUniqueId())) {
            this.alivePlayers.remove(player.getUniqueId());
            ServerUtilities.sendBroadcastMessage("§c§l" + player.getName() + "§4 abandonó");
            if (this.currentStatus == Status.PLAYING) {
                ItemStack[] items = player.getInventory().getContents();
                Location location = player.getLocation();
                Bukkit.getScheduler().runTaskLater(HungerGamesClassic.getInstance(), () -> {
                    for (ItemStack itemStack : items) {
                        if (itemStack != null) {
                            location.getWorld().dropItem(location, itemStack);
                        }
                    }
                }, 5L);
            }
        }
    }

    public void teleportPlayerToLobby(Player player) {
        Location lobbySpawn = ServerUtilities.getLobbySpawn();
        if (lobbySpawn != null) {
            player.teleport(lobbySpawn);
        } else {
            player.kickPlayer("§cEl lobby no ha sido cargado");
        }
    }

    public void restartPlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.setSaturation(0);
        player.setFireTicks(0);
        player.setAllowFlight(false);
        player.getActivePotionEffects().forEach(p -> player.removePotionEffect(p.getType()));
        player.setStatistic(Statistic.PLAYER_KILLS, 0);
        player.setStatistic(Statistic.DEATHS, 0);
        player.setBedSpawnLocation(ServerUtilities.getLobbySpawn());
        player.getInventory().clear();
        player.updateInventory();
    }

    public void preStartGame() {
        ServerUtilities.sendBroadcastMessage("§3§lPreparando la partida, teletransporte en 10 segundos al mapa");
        this.currentArena = ArenaController.getInstance().getRandomArena();
        this.currentArena.configureWorld();
        if (this.currentArena.getWorld() != null) {
            this.currentArena.fillChests();
            this.currentArena.getWorld().getWorldBorder().setSize(this.currentArena.getLobbyRadius());

            this.alivePlayers.clear();
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.playSound(p.getLocation(), "note.harp", 1, 1);
                this.alivePlayers.add(p.getUniqueId());
            });

            this.currentStatus = Status.WAITING;
            this.pvpEnabled = false;
            AtomicInteger remainingTime = new AtomicInteger(10);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (remainingTime.get() <= 0) {
                        teleportToGame();
                        cancel();
                    } else {
                        remainingTime.addAndGet(-1);
                    }
                }
            }.runTaskTimer(HungerGamesClassic.getInstance(), 0L, 20L);
        }
    }

    public void stopGame() {
        ServerUtilities.sendBroadcastMessage("§3§lLa partida ha terminado");
        ServerUtilities.playBroadcastSound("mob.wither.death", 1, 2);
        this.alivePlayers.clear();
        this.currentStatus = Status.LOBBY;
        Bukkit.getOnlinePlayers().forEach(player -> {
            this.restartPlayer(player);
            this.teleportPlayerToLobby(player);
            player.playSound(player.getLocation(), "note.harp", 1, 1);
        });
        this.currentArena.deleteWorld();
        this.currentArena = null;
        this.pvpEnabled = false;
        if (this.pvpEnabledTaskID != -1) {
            Bukkit.getScheduler().cancelTask(this.pvpEnabledTaskID);
            this.pvpEnabledTaskID = -1;
        }
    }

    public void teleportToGame() {
        this.currentArena.teleportPlayers();
        this.currentStatus = Status.FROZEN;
        KitController.getInstance().givePlayersKitSelector();
        AtomicInteger remainingTime = new AtomicInteger(60);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime.get() <= 0) {
                    gameStart();
                    cancel();
                } else {
                    if (remainingTime.get() % 10 == 0 || remainingTime.get() <= 3) {
                        ServerUtilities.sendBroadcastMessage("§3§lLa partida empieza en " + remainingTime.get() + " segundos");
                        ServerUtilities.playBroadcastSound("note.hat", 1, 1);
                    }
                    remainingTime.addAndGet(-1);
                }
            }
        }.runTaskTimer(HungerGamesClassic.getInstance(), 0L, 20L);
    }

    public void gameStart() {
        this.currentStatus = Status.PLAYING;
        ServerUtilities.sendBroadcastTitle("§bInicia el juego", "PvP en 60 s");
        ServerUtilities.playBroadcastSound("mob.wither.spawn", 1, 2);
        this.currentArena.getWorld().getWorldBorder().setSize(this.currentArena.getMaxRadius());
        KitController.getInstance().givePlayersKits();
        this.pvpEnabledScheduler();
    }

    public void pvpEnabledScheduler() {
        if (this.pvpEnabledTaskID != -1) {
            Bukkit.getScheduler().cancelTask(this.pvpEnabledTaskID);
        }
        AtomicInteger remainingTime = new AtomicInteger(60);
        this.pvpEnabledTaskID = new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime.get() <= 0) {
                    pvpEnabled = true;
                    ServerUtilities.sendBroadcastMessage("§3§lPvP habilitado, buena suerte");
                    ServerUtilities.playBroadcastSound("fireworks.blast", 1, 1);
                    cancel();
                } else {
                    if (remainingTime.get() % 10 == 0 || remainingTime.get() <= 3) {
                        ServerUtilities.sendBroadcastMessage("§3§lPvP habilitado en: " + remainingTime.get() + " segundos");
                        ServerUtilities.playBroadcastSound("note.hat", 1, 1);
                    }
                    remainingTime.addAndGet(-1);
                }
            }
        }.runTaskTimer(HungerGamesClassic.getInstance(), 0L, 20L).getTaskId();
    }

    public void validateWin() {
        if (this.alivePlayers.size() == 1) {
            Player winner = Bukkit.getPlayer(this.alivePlayers.get(0));
            if (winner != null) {
                ServerUtilities.sendBroadcastMessage("§bGanó " + winner.getName() + ", muchas felicidades~");
                ServerUtilities.sendBroadcastTitle("§bGanó " + winner.getName(), "¡Felicidades!");
            } else {
                ServerUtilities.sendBroadcastMessage("§bNo hay ganador, wtf, que raro...");
                ServerUtilities.sendBroadcastTitle("§bNo hay ganador", "Wtf, nadie ganó");
            }
        } else if (this.alivePlayers.size() == 0) {
            ServerUtilities.sendBroadcastMessage("§bNo hay ganador, wtf, que raro...");
            ServerUtilities.sendBroadcastTitle("§bNo hay ganador", "Wtf, nadie ganó");
        }
        this.stopGame();
    }

}
