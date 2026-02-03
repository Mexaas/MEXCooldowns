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
import org.axc.mexcooldowns.VersionAdapter.VersionAdapter;
import org.axc.mexcooldowns.VersionAdapter.VersionResolver;
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
        VersionAdapter adapter = VersionResolver.getAdapter();
        String baseValue = event.getMessage().trim()
                .replaceFirst("^/+", "")
                .split(" ")[0].toLowerCase();
        String command = baseValue.contains(":") ? baseValue.substring(baseValue.indexOf(":") + 1) : baseValue;
        long now = System.currentTimeMillis();

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
            if (Mexcooldowns.getInstance().getConfig().getInt("actionbar.duration") >= 31
            && Mexcooldowns.getInstance().getConfig().getBoolean("actionbar.enabled")) {
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
            int seconds = Mexcooldowns.getInstance().getConfig().getInt("groups." + highGroup.getName() + "." + command);
            long nextUse = now + seconds * 1000L;
            if ((command.equals(fromMapName) && now < fromMapUse) && Mexcooldowns.getInstance().getConfig().getBoolean("actionbar.enabled")) {
                ActionBarManager.sendActionBarMessage(Mexcooldowns.getInstance().getConfig().getString(("actionbar.message")),
                        event.getPlayer(),
                        fromMapUse,
                        fromMapName,
                        Mexcooldowns.getInstance().getConfig().getInt("actionbar.duration")
                );
                event.setCancelled(true);
                return;

            } else if (((command.equals(fromMapName) && now < fromMapUse) && !Mexcooldowns.getInstance().getConfig().getBoolean("actionbar.enabled"))) {
                event.getPlayer().sendMessage(adapter.parseHoldersMessage(
                        Objects.requireNonNull(Mexcooldowns.getInstance().getConfig().getString("messages.cooldown-active")),
                        FormatManager.setTimeFormat(Math.round((float) (fromMapUse - now) / 1000.0)),
                        command
                ));
                event.setCancelled(true);
                return;
            }
            CooldownManager.addCooldownUser(event.getPlayer(), nextUse, command);
            if (Mexcooldowns.getInstance().getConfig().getBoolean("actionbar.enabled")) {
                ActionBarManager.sendActionBarMessage(Mexcooldowns.getInstance().getConfig().getString("actionbar.message"),
                        event.getPlayer(),
                        nextUse,
                        command,
                        Mexcooldowns.getInstance().getConfig().getInt("actionbar.duration")
                );
            }
        } else {
            return;
        }
    }
}
