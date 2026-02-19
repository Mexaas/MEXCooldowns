package org.axc.mexcooldowns;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import org.axc.mexcooldowns.Backend.CooldownManager;
import org.axc.mexcooldowns.Backend.SendMessageEvent;
import org.axc.mexcooldowns.Commands.reloadCommand;
import org.axc.mexcooldowns.Notifiers.CooldownNotifier;
import org.axc.mexcooldowns.Notifiers.NotifierResolver;
import org.axc.mexcooldowns.VersionAdapter.VersionResolver;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Mexcooldowns extends JavaPlugin {
    public static Mexcooldowns instance;

    public static Mexcooldowns plugin() {
        return Mexcooldowns.getInstance();
    }
    public static Mexcooldowns getInstance() {
        return instance;
    }
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        NotifierResolver.initNotifier();

        getCommand("mexc").setExecutor(new reloadCommand());
        getServer().getPluginManager().registerEvents(new SendMessageEvent(updateRecordConfig()), this);
        saveResource("data.yml", false);
        VersionResolver.initVersion();
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
    public SendMessageEvent.ConfigValues updateRecordConfig() {
        ConfigurationSection messagesSection = getConfig().getConfigurationSection("messages");
        SendMessageEvent.ConfigValues configValues = new SendMessageEvent.ConfigValues(
                messagesSection.getString("no-permission"),
                messagesSection.getString("reload-success"),
                messagesSection.getString("actionbar-message"),
                messagesSection.getString("cooldown-active"),
                getConfig().getConfigurationSection("actionbar")
        );
    }
}
