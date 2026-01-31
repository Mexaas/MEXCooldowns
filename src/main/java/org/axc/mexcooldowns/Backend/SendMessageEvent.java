package org.axc.mexcooldowns.Backend;

import java.util.Map;
import java.util.UUID;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.axc.mexcooldowns.Mexcooldowns;
import org.axc.mexcooldowns.PluginUtils.Functions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SendMessageEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMessage(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/")) {
            return;
        }
        String baseValue = event.getMessage().trim()
                .replaceFirst("^/+", "")
                .split(" ")[0].toLowerCase();
        String command = baseValue.contains(":") ? baseValue.substring(baseValue.indexOf(":") + 1) : baseValue;
        long now = System.currentTimeMillis();
        Long fromMapUse = 0L;
        String fromMapName = null;
        Map<UUID, Map<Long, String>> cooldowns = CooldownManager.getCooldownsInstance();
        Map<Long, String> userInfo = cooldowns.get(event.getPlayer().getUniqueId());
        if (userInfo != null)
            for (Map.Entry<Long, String> cd : userInfo.entrySet()) {
                if (cd.getValue().equals(command)) {
                    fromMapUse = cd.getKey();
                    fromMapName = cd.getValue();
                    break;
                }
            }
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
            String path = "groups." + highGroup.getName() + "." + command;
            String pathBypass = "groups." + highGroup.getName() + ".bypass";
            if (!Mexcooldowns.getInstance().getConfig().contains(path)) {
                return;
            }
            if (Mexcooldowns.getInstance().getConfig().contains(pathBypass)) {
                return;
            }
            int seconds = Mexcooldowns.getInstance().getConfig().getInt("groups." + highGroup.getName() + "." + command);
            long nextUse = now + seconds * 1000L;
            if (command.equals(fromMapName) && now < fromMapUse) {
                event.getPlayer().sendMessage(Functions.getConfigValue("messages.cooldown-active")
                        .replace("%time%", FormatManager.setTimeFormat((Math.round(fromMapUse - now)) / 1000))
                        .replace("%command%", command));
                event.setCancelled(true);
                return;
            }
            CooldownManager.addCooldownUser(event.getPlayer(), nextUse, command);
        } else {
            return;
        }
    }
}
