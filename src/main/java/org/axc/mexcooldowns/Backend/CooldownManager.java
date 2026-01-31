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
    private static final Map<UUID, Map<Long, String>> cooldowns = new HashMap<>();

    public static Map<UUID, Map<Long, String>> getCooldownsInstance() {
        return cooldowns;
    }
    public static void addCooldownUser(Player player, Long nextUseAt, String command) {
        cooldowns.computeIfAbsent(player.getUniqueId(), id -> new HashMap<>())
                .put(nextUseAt, command);
    }

    public static void saveCooldownData() {
        File file = new File(Mexcooldowns.getInstance().getDataFolder(), "data.yml");
        YamlConfiguration data = new YamlConfiguration();
        ConfigurationSection users = data.createSection("users");

        for (Map.Entry<UUID, Map<Long, String>> entry : cooldowns.entrySet()) {
            UUID uuid = entry.getKey();
            ConfigurationSection cdSection = users.createSection(uuid.toString());
            Map<Long, String> userInfo = entry.getValue();

            for (Map.Entry<Long, String> cd : userInfo.entrySet())
                cdSection.set(String.valueOf(cd.getKey()), cd.getValue());
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
            for (String uuid : usersSection.getKeys(false)) {
                long now = System.currentTimeMillis();
                UUID userID = UUID.fromString(uuid);
                ConfigurationSection cdSection = usersSection.getConfigurationSection(String.valueOf(userID));

                Map<Long, String> hash = new HashMap<>();
                if (cdSection != null && !cdSection.getKeys(false).isEmpty())
                    for (String key : cdSection.getKeys(false)) {
                        long nextUseAt = Long.parseLong(key);
                        if (now >= nextUseAt)
                            continue;
                        String commandName = cdSection.getString(key);
                        hash.put(nextUseAt, commandName);
                    }
                if (!hash.isEmpty())
                    cooldowns.put(userID, hash);
            }
        }
    }
}
