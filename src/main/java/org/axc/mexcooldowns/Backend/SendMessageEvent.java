package org.axc.mexcooldowns.Backend;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.axc.mexcooldowns.Mexcooldowns;
import org.axc.mexcooldowns.Notifiers.CooldownNotifier;
import org.axc.mexcooldowns.VersionAdapter.VersionAdapter;
import org.axc.mexcooldowns.VersionAdapter.VersionResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SendMessageEvent implements Listener {
    private final VersionAdapter adapter = VersionResolver.getAdapter();
    private final ConfigValues configValues;
    private final FileConfiguration config = Mexcooldowns.getInstance().getConfig();
    public record ConfigValues(
            String noPermissionMessage,
            String reloadMessage,
            String actionBarMessage,
            String cooldownMessage,
            ConfigurationSection actionbar
    ) {}
    public SendMessageEvent(ConfigValues configValues) {
        this.configValues = configValues;
    }

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
            String groupPath = "groups." + highGroup.getName();
            if (config.contains(groupPath + "." + command) || !config.contains(groupPath + ".bypass")) {
                if (configValues.actionbar.getInt("duration") >= 31 && configValues.actionbar.getBoolean("enabled")) {
                    event.getPlayer().sendMessage(
                            adapter.parseMessage("&x&E&0&B&E&6&1WARNING:   &x&D&1&C&3&E&9Value in &x&E&0&B&E&6&1actionbar.duration must be <= &x&E&0&B&E&6&130"
                                    + "\n&x&E&0&B&E&6&1WARNING:   &x&D&1&C&3&E&9This is &x&E&0&B&E&6&1MEXCooldowns config &x&D&1&C&3&E&9Warning"));
                    event.setCancelled(true);
                    return;
                }

                Long fromMapUse = 0L;
                String fromMapName = null;
                Map<UUID, Map<Long, String>> cooldowns = CooldownManager.getCooldownsInstance();
                Map<Long, String> userInfo = cooldowns.get(event.getPlayer().getUniqueId());
                if (userInfo != null) {
                    Iterator<Map.Entry<Long, String>> iterator = userInfo.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Long, String> entry = iterator.next();
                        if (!Mexcooldowns.getInstance().getConfig()
                                .contains("groups." + highGroup.getName() + "." + entry.getValue())) {
                            iterator.remove();
                        }
                        if (entry.getValue().equals(command)) {
                            fromMapUse = entry.getKey(); fromMapName = entry.getValue();
                            break;
                        }
                    }
                }
                int seconds = config.getInt(groupPath + "." + command); long nextUse = now + seconds * 1000L;
                if ((command.equals(fromMapName) && now < fromMapUse) && config.getBoolean("actionbar.enabled")) {
                    ActionBarManager.sendActionBarMessage(config.getString(("actionbar.message")),
                            event.getPlayer(),
                            fromMapUse,
                            fromMapName,
                            config.getInt("actionbar.duration")
                    );
                    event.setCancelled(true);
                    return;

                } else if (((command.equals(fromMapName) && now < fromMapUse) && !config.getBoolean("actionbar.enabled"))) {
                    event.getPlayer().sendMessage(adapter.parseHoldersMessage(
                            Objects.requireNonNull(config.getString("messages.cooldown-active")),
                            FormatManager.setTimeFormat(Math.round((float) (fromMapUse - now) / 1000.0)),
                            command
                    ));
                    event.setCancelled(true);
                    return;
                }
                CooldownManager.addCooldownUser(event.getPlayer(), nextUse, command);
                if (config.getBoolean("actionbar.enabled")) {
                    ActionBarManager.sendActionBarMessage(config.getString("actionbar.message"),
                            event.getPlayer(),
                            nextUse,
                            command,
                            config.getInt("actionbar.duration")
                    );
                }
            }
        }
    }
}
