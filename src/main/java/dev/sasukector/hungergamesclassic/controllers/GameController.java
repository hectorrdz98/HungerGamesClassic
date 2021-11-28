package dev.sasukector.hungergamesclassic.controllers;

import dev.sasukector.hungergamesclassic.HungerGamesClassic;
import dev.sasukector.hungergamesclassic.helpers.ServerUtilities;
import dev.sasukector.hungergamesclassic.models.Arena;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameController {

    private static GameController instance = null;
    private @Getter Status currentStatus = Status.LOBBY;
    private final @Getter List<UUID> alivePlayers;
    private @Getter Arena currentArena = null;
    private @Getter boolean pvpEnabled = false;
    private final @Getter boolean streamerMode = false;
    private int pvpEnabledTaskID = -1;
    private int updateCompassTaskID = -1;
    private int reduceBorderTaskID = -1;
    private final int minRequiredPlayers = 2;
    private boolean gameStarting = false;

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
        if (this.currentStatus == Status.FROZEN || this.currentStatus == Status.PLAYING) {
            player.sendTitle(new Title("§7Espectador", "Espera al final"));
            player.setGameMode(GameMode.SPECTATOR);
            this.currentArena.teleportPlayer(player);
        } else {
            player.sendTitle(new Title("§d¡Bienvenido!", "Espera al comienzo"));
            ServerUtilities.sendServerMessage(player, "§3Bienvenido, haz click derecho con la espada para §bunirte a la cola§3.");
            if (this.currentStatus == Status.WAITING) {
                ServerUtilities.sendServerMessage(player, "§9¡Una partida está por empezar!");
            }
            this.givePlayerLobbyItems(player);
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
                this.validateWin();
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
        player.getEquipment().setArmorContents(new ItemStack[]{ null, null, null, null });
        player.updateInventory();
    }

    public void givePlayerLobbyItems(Player player) {
        ItemStack joinQueue = new ItemStack(Material.STONE_SWORD);
        joinQueue.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta bookMeta = joinQueue.getItemMeta();
        bookMeta.setDisplayName("§dUnirse a cola");
        bookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        joinQueue.setItemMeta(bookMeta);
        player.getInventory().addItem(joinQueue.clone());
    }

    public void checkIfGamePossible() {
        if (this.currentStatus == Status.LOBBY && this.alivePlayers.size() >= this.minRequiredPlayers && !this.gameStarting) {
            ServerUtilities.sendBroadcastMessage("§3Hay suficientes jugadores");
            this.gameStarting = true;
            AtomicInteger remainingTime = new AtomicInteger(30);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (remainingTime.get() <= 0) {
                        preStartGame();
                        cancel();
                    } else {
                        if (remainingTime.get() % 15 == 0 || remainingTime.get() <= 3) {
                            ServerUtilities.sendBroadcastMessage("§3Teletransporte en §b" + remainingTime.get() + " segundos");
                            ServerUtilities.playBroadcastSound("note.hat", 1, 1);
                        }
                        ServerUtilities.sendBroadcastAction("§3Teletransporte en §b" + remainingTime.get() + " s");
                        remainingTime.addAndGet(-1);
                    }
                }
            }.runTaskTimer(HungerGamesClassic.getInstance(), 0L, 20L);
        }
    }

    public void addAllPlayersToGame() {
        this.alivePlayers.clear();
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.playSound(p.getLocation(), "note.harp", 1, 1);
            this.alivePlayers.add(p.getUniqueId());
        });
    }

    public void preStartGame() {
        this.currentArena = ArenaController.getInstance().getRandomArena();
        this.currentArena.configureWorld();
        if (this.currentArena.getWorld() != null) {
            this.currentArena.fillChests();
            this.currentArena.getWorld().getWorldBorder().setSize(this.currentArena.getLobbyRadius());

            Bukkit.getOnlinePlayers().forEach(this::restartPlayer);
            if (this.streamerMode) {
                this.addAllPlayersToGame();
            }

            this.currentStatus = Status.WAITING;
            this.pvpEnabled = false;
            this.teleportToGame();
        }
    }

    public void stopGame() {
        ServerUtilities.sendBroadcastMessage("§3§lLa partida ha terminado");
        ServerUtilities.playBroadcastSound("mob.wither.death", 1, 2);
        this.alivePlayers.clear();
        this.currentStatus = Status.LOBBY;
        this.gameStarting = false;
        Bukkit.getOnlinePlayers().forEach(player -> {
            this.restartPlayer(player);
            this.teleportPlayerToLobby(player);
            this.givePlayerLobbyItems(player);
            player.playSound(player.getLocation(), "note.harp", 1, 1);
        });
        this.currentArena.deleteWorld();
        this.currentArena = null;
        this.pvpEnabled = false;
        if (this.pvpEnabledTaskID != -1) {
            Bukkit.getScheduler().cancelTask(this.pvpEnabledTaskID);
            this.pvpEnabledTaskID = -1;
        }
        if (this.updateCompassTaskID != -1) {
            Bukkit.getScheduler().cancelTask(this.updateCompassTaskID);
            this.updateCompassTaskID = -1;
        }
        if (this.reduceBorderTaskID != -1) {
            Bukkit.getScheduler().cancelTask(this.reduceBorderTaskID);
            this.reduceBorderTaskID = -1;
        }
    }

    public void teleportToGame() {
        this.currentArena.teleportPlayers();
        this.currentStatus = Status.FROZEN;
        KitController.getInstance().givePlayersKitSelector();
        this.updatePlayerCompassSchedule();
        AtomicInteger remainingTime = new AtomicInteger(30);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime.get() <= 0) {
                    if (alivePlayers.size() >= minRequiredPlayers) {
                        gameStart();
                    } else {
                        ServerUtilities.sendBroadcastMessage("§cNo hay suficientes jugadores, regresando al lobby");
                        stopGame();
                    }
                    cancel();
                } else {
                    if (remainingTime.get() % 10 == 0 || remainingTime.get() <= 3) {
                        ServerUtilities.sendBroadcastMessage("§3La partida empieza en §b" + remainingTime.get() + " segundos");
                        ServerUtilities.playBroadcastSound("note.hat", 1, 1);
                    }
                    ServerUtilities.sendBroadcastAction("§3Comienzo en §b" + remainingTime.get() + " s");
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
        this.reduceBorderScheduler();
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
                    ServerUtilities.sendBroadcastMessage("§6§lPvP habilitado, buena suerte");
                    ServerUtilities.playBroadcastSound("fireworks.blast", 1, 1);
                    cancel();
                } else {
                    if (remainingTime.get() % 10 == 0 || remainingTime.get() <= 3) {
                        ServerUtilities.sendBroadcastMessage("§3PvP habilitado en §b" + remainingTime.get() + " segundos");
                        ServerUtilities.playBroadcastSound("note.hat", 1, 1);
                    }
                    ServerUtilities.sendBroadcastAction("§3PvP en §b" + remainingTime.get() + " s");
                    remainingTime.addAndGet(-1);
                }
            }
        }.runTaskTimer(HungerGamesClassic.getInstance(), 0L, 20L).getTaskId();
    }

    public void validateWin() {
        boolean validWin = false;
        if (this.alivePlayers.size() == 1) {
            Player winner = Bukkit.getPlayer(this.alivePlayers.get(0));
            if (winner != null) {
                ServerUtilities.sendBroadcastMessage("§bGanó " + winner.getName() + ", muchas felicidades~");
                ServerUtilities.sendBroadcastTitle("§bGanó " + winner.getName(), "¡Felicidades!");
            } else {
                ServerUtilities.sendBroadcastMessage("§bNo hay ganador, wtf, que raro...");
                ServerUtilities.sendBroadcastTitle("§bNo hay ganador", "Wtf, nadie ganó");
            }
            validWin = true;
        } else if (this.alivePlayers.size() == 0) {
            ServerUtilities.sendBroadcastMessage("§bNo hay ganador, wtf, que raro...");
            ServerUtilities.sendBroadcastTitle("§bNo hay ganador", "Wtf, nadie ganó");
            validWin = true;
        }
        if (validWin) {
            this.currentStatus = Status.LOBBY;
            ServerUtilities.sendBroadcastMessage("§3Regreso al lobby en 10 segundos");
            AtomicInteger remainingTime = new AtomicInteger(10);
            this.pvpEnabledTaskID = new BukkitRunnable() {
                @Override
                public void run() {
                    if (remainingTime.get() <= 0) {
                        stopGame();
                        cancel();
                    } else {
                        ServerUtilities.sendBroadcastAction("§3Regreso en §b" + remainingTime.get() + " s");
                        remainingTime.addAndGet(-1);
                    }
                }
            }.runTaskTimer(HungerGamesClassic.getInstance(), 0L, 20L).getTaskId();
        }
    }

    public void updatePlayerCompassSchedule() {
        if (this.updateCompassTaskID != -1) {
            Bukkit.getScheduler().cancelTask(this.updateCompassTaskID);
        }
        this.updateCompassTaskID = new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : alivePlayers) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) continue;
                    Location location = player.getLocation();
                    Optional<UUID> nearestPlayerUUID = alivePlayers.stream()
                            .filter(nearUUID -> nearUUID != uuid && Bukkit.getPlayer(nearUUID) != null)
                            .min(Comparator.comparingDouble(value -> {
                                Player nearPlayer = Bukkit.getPlayer(value);
                                return location.distance(nearPlayer.getLocation());
                            }));
                    if (nearestPlayerUUID.isPresent()) {
                        Player nearestPlayer = Bukkit.getPlayer(nearestPlayerUUID.get());
                        if (nearestPlayer == null) continue;
                        player.setCompassTarget(nearestPlayer.getLocation());
                    }
                }
            }
        }.runTaskTimer(HungerGamesClassic.getInstance(), 0L, 20L).getTaskId();
    }

    public void reduceBorderScheduler() {
        if (this.reduceBorderTaskID != -1) {
            Bukkit.getScheduler().cancelTask(this.reduceBorderTaskID);
        }
        this.reduceBorderTaskID = new BukkitRunnable() {
            @Override
            public void run() {
                ServerUtilities.sendBroadcastMessage("§5Reduciendo el borde");
                currentArena.getWorld().getWorldBorder().setSize(currentArena.getLobbyRadius(), 10 * 60);
            }
        }.runTaskLater(HungerGamesClassic.getInstance(), 20L * 60 * 15).getTaskId();
    }

}
