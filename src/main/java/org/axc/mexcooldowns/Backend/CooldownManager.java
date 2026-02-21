package org.axc.mexcooldowns.Backend;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.axc.mexcooldowns.Mexcooldowns;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class CooldownManager {
    private static final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public static Map<UUID, Map<String, Long>> getCooldownsInstance() {
        return cooldowns;
    }
    public static void addCooldownUser(Player player, Long time, String command) {
        cooldowns.computeIfAbsent(player.getUniqueId(), id -> new HashMap<>())
                .put(command, time);
    }

    public static void saveCooldownData() {
        File file = new File(Mexcooldowns.getInstance().getDataFolder(), "data.yml");
        YamlConfiguration data = new YamlConfiguration();
        ConfigurationSection users = data.createSection("users");

        for (Map.Entry<UUID, Map<String, Long>> entry : cooldowns.entrySet()) {
            UUID uuid = entry.getKey();
            ConfigurationSection cooldownsSection = users.createSection(uuid.toString());
            Map<String, Long> userData = entry.getValue();

            for (Map.Entry<String, Long> cd : userData.entrySet())
                cooldownsSection.set(cd.getKey(), cd.getValue());
        }
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void loadCooldownData() {
        File file = new File(Mexcooldowns.getInstance().getDataFolder(), "data.yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection usersSection = data.getConfigurationSection("users");

        if (usersSection != null && !usersSection.getKeys(false).isEmpty()) {
            long now = System.currentTimeMillis();
            for (String uuid : usersSection.getKeys(false)) {
                UUID userID = UUID.fromString(uuid);
                Map<String, Long> userCooldownsData = cooldowns.computeIfAbsent(userID, id -> new HashMap<>());
                ConfigurationSection userYamlData = usersSection.getConfigurationSection(String.valueOf(userID));

                if (userYamlData != null && !userYamlData.getKeys(false).isEmpty()) {
                    for (String key : userYamlData.getKeys(false)) {
                        long time = userYamlData.getLong(key);
                        if (now >= time)
                            continue;
                        userCooldownsData.put(key, time);
                    }
                }
            }
        }
    }
}
