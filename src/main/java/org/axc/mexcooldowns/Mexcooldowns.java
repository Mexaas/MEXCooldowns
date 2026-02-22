package org.axc.mexcooldowns;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import org.axc.mexcooldowns.Backend.CooldownManager;
import org.axc.mexcooldowns.Backend.FormatManager;
import org.axc.mexcooldowns.Backend.SendMessageEvent;
import org.axc.mexcooldowns.Commands.reloadCommand;
import org.axc.mexcooldowns.Notifiers.NotifierResolver;
import org.axc.mexcooldowns.VersionAdapter.VersionResolver;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public final class Mexcooldowns extends JavaPlugin {
    public static Mexcooldowns instance;

    public static Mexcooldowns getInstance() {
        return instance;
    }
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        getCommand("mexc").setExecutor(new reloadCommand(this));
        getServer().getPluginManager().registerEvents(new SendMessageEvent(this), this);
        saveResource("data.yml", false);
        VersionResolver.initVersion();
        CooldownManager.loadCooldownData();
        NotifierResolver.initNotifier();

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            long now = System.currentTimeMillis();
            Map<UUID, Map<String, Long>> dataMap = CooldownManager.getCooldownsInstance();

            for (UUID userID : new HashSet<>(dataMap.keySet())) {
                Map<String, Long> userData = dataMap.get(userID);
                for (String key : new HashSet<>(userData.keySet())) {
                    if (userData.get(key) < now) {
                        userData.remove(key);
                    }
                }
                if (userData.isEmpty()) {
                    dataMap.remove(userID);
                }
            }
        }, 20L, 20L);
    }
    public void onDisable() {
        CooldownManager.saveCooldownData();
    }
}
