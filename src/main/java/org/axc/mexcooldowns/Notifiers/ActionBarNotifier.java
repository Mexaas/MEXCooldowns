package org.axc.mexcooldowns.Notifiers;

import org.axc.mexcooldowns.Backend.FormatManager;
import org.axc.mexcooldowns.Mexcooldowns;
import org.axc.mexcooldowns.VersionAdapter.VersionAdapter;
import org.axc.mexcooldowns.VersionAdapter.VersionResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActionBarNotifier implements CooldownNotifier {
    public static Map<UUID, BukkitTask> actionBars = new HashMap<>();

    @Override
    public void sendNotification(Player user, long nextUse, String commandName) {
        ConfigurationSection messages = Mexcooldowns.getInstance().getConfig().getConfigurationSection("messages");
        ConfigurationSection actionBar = Mexcooldowns.getInstance().getConfig().getConfigurationSection("actionbar");
        if (actionBars.containsKey(user.getUniqueId())) {
            actionBars.get(user.getUniqueId()).cancel();

            actionBars.remove(user.getUniqueId());
        }
        VersionAdapter adapter = VersionResolver.getAdapter();
        BukkitTask playerActionBar = new BukkitRunnable() {
            int duration = 0;

            @Override
            public void run() {
                long now = System.currentTimeMillis();
                if (now >= nextUse || !user.isOnline() || duration >= actionBar.getInt("duration")) {
                    cancel();
                    return;
                }
                user.sendActionBar(adapter.parseHoldersMessage(
                        messages.getString("actionbar-message"),
                        FormatManager.setTimeFormat(Math.round((float) (nextUse - now) / 1000.0)),
                        commandName
                        )
                );
                duration++;
            }
        }.runTaskTimer(Mexcooldowns.getInstance(), 0L, 20L);
        actionBars.put(user.getUniqueId(), playerActionBar);
    }
}
