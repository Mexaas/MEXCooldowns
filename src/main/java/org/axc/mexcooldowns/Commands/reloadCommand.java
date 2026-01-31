package org.axc.mexcooldowns.Commands;

import org.axc.mexcooldowns.PluginUtils.Functions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class reloadCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            if (strings.length >= 1 && strings[0].equalsIgnoreCase("reload")) {
                if (player.isOp() || player.hasPermission("mexcooldowns.reload")) {
                    player.sendMessage(Functions.configPrefix() + Functions.configPrefix());
                    Functions.configReload();
                    return true;
                }
                player.sendMessage(Functions.configPrefix() + Functions.configPrefix());
                return true;
            }
            player.sendMessage(Functions.helpPlugin());
        } else if (commandSender instanceof ConsoleCommandSender) {
            Functions.configReload();
            return true;
        }
        return false;
    }
}
