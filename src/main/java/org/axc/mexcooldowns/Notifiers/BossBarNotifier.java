package org.axc.mexcooldowns.Notifiers;

import net.kyori.adventure.text.Component;
import org.axc.mexcooldowns.Backend.FormatManager;
import org.axc.mexcooldowns.Mexcooldowns;
import org.axc.mexcooldowns.VersionAdapter.VersionAdapter;
import org.axc.mexcooldowns.VersionAdapter.VersionResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarNotifier implements CooldownNotifier {
    public static Map<UUID, BossBar> activeBossBars = new HashMap<>();
    public static Map<UUID, BukkitTask> activeBukkitTasks = new HashMap<>();

    @Override
    public void sendNotification(Player user, long nextUse, String commandName) {
        ConfigurationSection messages = Mexcooldowns.getInstance().getConfig().getConfigurationSection("messages");
        ConfigurationSection bossBar = Mexcooldowns.getInstance().getConfig().getConfigurationSection("bossbar");
        if (activeBukkitTasks.containsKey(user.getUniqueId())) {
            activeBukkitTasks.get(user.getUniqueId()).cancel();

            activeBukkitTasks.remove(user.getUniqueId());

            if (activeBossBars.containsKey(user.getUniqueId())) {
                user.hideBossBar(activeBossBars.get(user.getUniqueId()));

                activeBossBars.remove(user.getUniqueId());
            }
        }
        BossBar bar = BossBar.bossBar(
                Component.text("Cooldown"),
                1.0F,
                Color.GREEN,
                Overlay.PROGRESS
        );
        user.showBossBar(bar);

        VersionAdapter adapter = VersionResolver.getAdapter();
        BukkitTask bossBarRunnable = new BukkitRunnable() {
            int maxDuration = bossBar.getInt("duration");
            long startTime = System.currentTimeMillis();
            long total = nextUse - startTime;

            @Override
            public void run() {
                long now = System.currentTimeMillis();

                if (now >= nextUse || !user.isOnline() || maxDuration <= 0) {
                    user.hideBossBar(bar); cancel();
                    return;
                }
                double secondsLeft = (nextUse - now) / 1000.0;
                bar.name(adapter.parseHoldersMessage(
                        messages.getString("bossbar-message"),
                        FormatManager.setTimeFormat(Math.round(secondsLeft)),
                        commandName
                        )
                );
                bar.progress((float) Math.max(0.0, Math.min(1.0, (double) (nextUse - now) / total)));
                maxDuration--;
            }
        }.runTaskTimer(Mexcooldowns.getInstance(), 0L, 20L);

        activeBossBars.put(user.getUniqueId(), bar);
        activeBukkitTasks.put(user.getUniqueId(), bossBarRunnable);
    }
}
