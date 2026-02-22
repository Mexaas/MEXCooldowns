package org.axc.mexcooldowns.Commands;

import net.kyori.adventure.text.Component;
import org.axc.mexcooldowns.Backend.SendMessageEvent;
import org.axc.mexcooldowns.Mexcooldowns;
import org.axc.mexcooldowns.Notifiers.NotifierResolver;
import org.axc.mexcooldowns.VersionAdapter.VersionAdapter;
import org.axc.mexcooldowns.VersionAdapter.VersionResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class reloadCommand implements CommandExecutor {
    private final Mexcooldowns plugin;
    public reloadCommand(Mexcooldowns plugin) {
        this.plugin = plugin;
    }
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            VersionAdapter adapter = VersionResolver.getAdapter();
            ConfigurationSection messages = plugin.getConfig().getConfigurationSection("messages");
            if (strings.length >= 1 && strings[0].equalsIgnoreCase("reload")) {
                Component prefixComponent = adapter.parseHoldersMessage(plugin.getConfig().getString("prefix"));
                if (player.isOp() || player.hasPermission("mexcooldowns.reload")) {
                    player.sendMessage(prefixComponent.append(adapter.parseHoldersMessage(messages.getString("reload-success"))));
                    plugin.reloadConfig();

                    NotifierResolver.initNotifier();
                    return true;
                }
                player.sendMessage(prefixComponent.append(adapter.parseHoldersMessage(messages.getString("no-permission"))));
                return true;
            }
            for (String text : messages.getStringList("help-message")) {
                player.sendMessage(adapter.parseHoldersMessage(text));
            }
            return true;
        } else if (commandSender instanceof ConsoleCommandSender) {
            plugin.reloadConfig();
            return true;
        }
        return false;
    }
}
