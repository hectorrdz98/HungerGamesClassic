package dev.sasukector.hungergamesclassic.commands;

import dev.sasukector.hungergamesclassic.controllers.GameController;
import dev.sasukector.hungergamesclassic.helpers.ServerUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.isOp()) {
                if (args.length > 0) {
                    String option = args[0];
                    switch (option) {
                        case "start": {
                            if (GameController.getInstance().getCurrentStatus() == GameController.Status.LOBBY) {
                                player.playSound(player.getLocation(), "note.harp", 1, 1);
                                GameController.getInstance().preStartGame();
                            } else {
                                ServerUtilities.sendServerMessage(player, "§cYa hay una partida en curso");
                            }
                        } break;
                        case "stop": {
                            if (GameController.getInstance().getCurrentStatus() == GameController.Status.PLAYING) {
                                player.playSound(player.getLocation(), "note.harp", 1, 1);
                                GameController.getInstance().stopGame();
                            } else {
                                ServerUtilities.sendServerMessage(player, "§cNo hay una partida en curso");
                            }
                        } break;
                    }
                }
            } else {
                ServerUtilities.sendServerMessage(player, "§cPermisos insuficientes");
            }
        }
        return true;
    }

}
