package org.axc.mexcooldowns.Backend;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.axc.mexcooldowns.Mexcooldowns;
import org.axc.mexcooldowns.Notifiers.NotifierResolver;
import org.axc.mexcooldowns.VersionAdapter.VersionAdapter;
import org.axc.mexcooldowns.VersionAdapter.VersionResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SendMessageEvent implements Listener {
    private final Mexcooldowns plugin;
    public SendMessageEvent(ConfigValues configValues, Mexcooldowns plugin) {
        this.configValues = configValues;
        this.plugin = plugin;
    }
    private final VersionAdapter adapter = VersionResolver.getAdapter();
    private final ConfigValues configValues;
    public record ConfigValues(
            ConfigurationSection actionbar,
            ConfigurationSection bossbar,
            ConfigurationSection messages
    ) {}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMessage(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/")) {
            return;
        }
        long now = System.currentTimeMillis();
        String baseValue = event.getMessage().trim().replaceFirst("^/+", "").split(" ")[0].toLowerCase();
        String command = baseValue.contains(":") ? baseValue.substring(baseValue.indexOf(":") + 1) : baseValue;

        LuckPerms luckperms = LuckPermsProvider.get();
        Group highGroup = null;
        int highestWeight = Integer.MIN_VALUE;
        User user = luckperms.getUserManager().getUser(event.getPlayer().getUniqueId());
        if (user != null) {
            for (Group group : user.getInheritedGroups(user.getQueryOptions())) {
                int weight = group.getWeight().orElse(0);
                if (weight > highestWeight) {
                    highGroup = group;
                    highestWeight = weight;
                }
            }
            FileConfiguration config = plugin.getConfig();
            String groupPath = "groups." + highGroup.getName();
            if (config.contains(groupPath + "." + command) || !config.contains(groupPath + ".bypass")) {
                if (configValues.actionbar.getInt("duration") >= 31 || configValues.bossbar.getInt("duration") >= 31) {
                    event.getPlayer().sendMessage(adapter.parseHoldersMessage(configValues.messages.getString("warning-message")));
                    event.setCancelled(true);
                    return;
                }
                Long commandMapTime = 0L;
                Map<String, Long> userData = CooldownManager.getCooldownsInstance().get(event.getPlayer().getUniqueId());
                if (userData != null && userData.containsKey(command)) {
                    commandMapTime = userData.get(command);
                }
                if (now < commandMapTime) {
                    new NotifierResolver().sendNotification(
                            event.getPlayer(),
                            commandMapTime,
                            command
                    );
                    event.setCancelled(true);
                    return;
                }
                int seconds = config.getInt(groupPath + "." + command);
                long nextCommandUse = now + seconds * 1000L;
                CooldownManager.addCooldownUser(event.getPlayer(), nextCommandUse, command);
            }
        }
    }
}