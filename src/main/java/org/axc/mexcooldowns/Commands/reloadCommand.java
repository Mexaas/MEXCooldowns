package org.axc.mexcooldowns.Commands;

import net.kyori.adventure.text.Component;
import org.axc.mexcooldowns.Backend.CooldownManager;
import org.axc.mexcooldowns.Mexcooldowns;
import org.axc.mexcooldowns.MiniMessage.TextFormat;
import org.axc.mexcooldowns.PluginUtils.Functions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class reloadCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            if (strings.length >= 1 && strings[0].equalsIgnoreCase("reload")) {
                Component prefixComponent = TextFormat.parse(Objects.requireNonNull(Mexcooldowns.getInstance().getConfig().getString("prefix")));
                if (player.isOp() || player.hasPermission("mexcooldowns.reload")) {
                    player.sendMessage(
                            prefixComponent.append(TextFormat.parse(Objects.requireNonNull(Mexcooldowns.getInstance().getConfig().getString("messages.reload-success"))))
                    );
                    Mexcooldowns.getInstance().reloadConfig();

                    return true;
                }
                player.sendMessage(
                        prefixComponent.append(TextFormat.parse(Objects.requireNonNull(Mexcooldowns.getInstance().getConfig().getString("messages.no-permission"))))
                );
                return true;
            }
            player.sendMessage(Functions.helpPlugin());
        } else if (commandSender instanceof ConsoleCommandSender) {
            Mexcooldowns.getInstance().reloadConfig();
            return true;
        }
        return false;
    }
}
