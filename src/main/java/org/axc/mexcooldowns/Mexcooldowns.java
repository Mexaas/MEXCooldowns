package org.axc.mexcooldowns;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import org.axc.mexcooldowns.Backend.CooldownManager;
import org.axc.mexcooldowns.Backend.SendMessageEvent;
import org.axc.mexcooldowns.Commands.reloadCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Mexcooldowns extends JavaPlugin {
    public static Mexcooldowns instance;

    public static Mexcooldowns getInstance() {
        return instance;
    }
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getCommand("mexc").setExecutor(new reloadCommand());
        getServer().getPluginManager().registerEvents(new SendMessageEvent(), this);
        saveResource("data.yml", false);

        CooldownManager.loadCooldownData();
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            long now = System.currentTimeMillis();
            Map<UUID, Map<Long, String>> dataMap = CooldownManager.getCooldownsInstance();

            for (UUID userID : new HashSet<>(dataMap.keySet())) {
                Map<Long, String> CDs = dataMap.get(userID);
                for (Long timestamp : new HashSet<>(CDs.keySet())) {
                    if (timestamp < now) {
                        CDs.remove(timestamp);
                    }
                }
                if (CDs.isEmpty()) {
                    dataMap.remove(userID);
                }
            }
        }, 20L, 20L);
    }

    public void onDisable() {
        CooldownManager.saveCooldownData();
    }
}
