package org.axc.mexcooldowns.Notifiers;

import org.axc.mexcooldowns.Mexcooldowns;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotifierResolver implements CooldownNotifier {
    private static List<CooldownNotifier> notifiers = new ArrayList<>();

    @Override
    public void sendNotification(Player user, long nextUse, String commandName) {
        for (CooldownNotifier notifier : notifiers) {
            notifier.sendNotification(user, nextUse, commandName);
        }
    }
    public static void add(CooldownNotifier notifier) {
        notifiers.add(notifier);
    }
    public static void initNotifier() {
        notifiers.clear();

        ConfigurationSection ActionBarSection = Mexcooldowns.getInstance().getConfig().getConfigurationSection("actionbar");
        ConfigurationSection BossBarSection = Mexcooldowns.getInstance().getConfig().getConfigurationSection("bossbar");
        if (ActionBarSection.getBoolean("enabled")) {
            notifiers.add(new ActionBarNotifier());
        }
        if (BossBarSection.getBoolean("enabled")) {
            notifiers.add(new BossBarNotifier());
        }
        if (notifiers.isEmpty()) {
            notifiers.add(new ChatNotifier());
        }
    }
    public List<CooldownNotifier> getNotifiers() {
        return notifiers;
    }
}
