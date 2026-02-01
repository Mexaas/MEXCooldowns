package org.axc.mexcooldowns.Backend;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.axc.mexcooldowns.Mexcooldowns;
import org.axc.mexcooldowns.MiniMessage.TextFormat;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActionBarManager {
    public static Map<UUID, BukkitTask> actionBars = new HashMap<>();

    public static void sendActionBarMessage(final String message, final Player user, final long nextUse, final String commandName, final int value) {
        if (actionBars.containsKey(user.getUniqueId())) {
            actionBars.get(user.getUniqueId()).cancel();

            actionBars.remove(user.getUniqueId());
        }
        BukkitTask playerActionBar = new BukkitRunnable() {
            int duration = 0;

            @Override
            public void run() {
                long now = System.currentTimeMillis();
                if (now >= nextUse || !user.isOnline() || duration >= value) {
                    cancel();
                    return;
                }
                user.sendActionBar(TextFormat.parseHolders(
                        message,
                        FormatManager.setTimeFormat(Math.round((float) (nextUse - now) / 1000.0)),
                        commandName
                ));
                duration++;
            }
        }.runTaskTimer(Mexcooldowns.getInstance(), 0L, 20L);
        Map<UUID, BukkitTask> actionBarMap = getActionBarHash();

        actionBarMap.put(user.getUniqueId(), playerActionBar);
    }

    public static Map<UUID, BukkitTask> getActionBarHash() {
        return actionBars;
    }
}
