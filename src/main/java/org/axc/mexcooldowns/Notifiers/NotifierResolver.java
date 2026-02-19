package org.axc.mexcooldowns.Notifiers;

import org.axc.mexcooldowns.Mexcooldowns;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotifierResolver implements CooldownNotifier {
    private static List<CooldownNotifier> notifiers = new ArrayList<>();
    private static final FileConfiguration config = Mexcooldowns.getInstance().getConfig();
    private static final ConfigurationSection ActionBarSection = config.getConfigurationSection("actionbar");
    private static final ConfigurationSection BossBarSection = config.getConfigurationSection("bossbar");

    public static void add(CooldownNotifier notifier) {
        notifiers.add(notifier);
    }
    @Override
    public void sendNotification() {
        for (CooldownNotifier notifier : notifiers) {
            notifier.sendNotification();
        }
    }
    public static void initNotifier() {
        if (ActionBarSection.getBoolean("enabled")) {
            notifiers.add(new ActionBarNotifier());
        }
        if (BossBarSection.getBoolean("enabled")) {
            notifiers.add(new BossBarNotifier());
        }
        notifiers.add(new ChatNotifier());
    }
    public List<CooldownNotifier> getNotifiers() {
        return notifiers;
    }
}
