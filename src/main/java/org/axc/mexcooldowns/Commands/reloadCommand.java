package org.axc.mexcooldowns.Commands;

import net.kyori.adventure.text.Component;
import org.axc.mexcooldowns.Mexcooldowns;
import org.axc.mexcooldowns.VersionAdapter.VersionAdapter;
import org.axc.mexcooldowns.VersionAdapter.VersionResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class reloadCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            VersionAdapter adapter = VersionResolver.getAdapter();
            if (strings.length >= 1 && strings[0].equalsIgnoreCase("reload")) {
                Component prefixComponent = adapter.parseMessage(Objects.requireNonNull(Mexcooldowns.getInstance().getConfig().getString("prefix")));
                if (player.isOp() || player.hasPermission("mexcooldowns.reload")) {
                    player.sendMessage(prefixComponent.append(adapter.parseMessage(Mexcooldowns.getInstance().getConfig().getString("messages.reload-success"))));
                    Mexcooldowns.getInstance().reloadConfig();

                    return true;
                }
                player.sendMessage(prefixComponent.append(adapter.parseMessage(Mexcooldowns.getInstance().getConfig().getString("messages.no-permission"))));
                return true;
            }
            player.sendMessage("\n               &x&6&5&5&F&D&BM&x&6&7&5&F&D&6E&x&6&9&6&0&D&2X&x&6&B&6&0&C&DC&x&6&D&6&0&C&8o&x&6&F&6&1&C&3o&x&7&0&6&1&B&Fl&x&7&2&6&2&B&Ad&x&7&4&6&2&B&5o&x&7&6&6&2&B&0w&x&7&8&6&3&A&Cn&x&7&A&6&3&A&7s\n\n&7  /mexcooldowns reload &8[Plugin reload]\n ");
        } else if (commandSender instanceof ConsoleCommandSender) {
            Mexcooldowns.getInstance().reloadConfig();
            return true;
        }
        return false;
    }
}
