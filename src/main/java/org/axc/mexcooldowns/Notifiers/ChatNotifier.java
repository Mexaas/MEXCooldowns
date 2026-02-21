package org.axc.mexcooldowns.Notifiers;

import org.axc.mexcooldowns.Backend.FormatManager;
import org.axc.mexcooldowns.Mexcooldowns;
import org.axc.mexcooldowns.VersionAdapter.VersionAdapter;
import org.axc.mexcooldowns.VersionAdapter.VersionResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ChatNotifier implements CooldownNotifier {
    @Override
    public void sendNotification(Player user, long nextUse, String commandName) {
        VersionAdapter adapter = VersionResolver.getAdapter();
        ConfigurationSection messages = Mexcooldowns.getInstance().getConfig().getConfigurationSection("messages");
        long now = System.currentTimeMillis();
        user.sendMessage(
                adapter.parseHoldersMessage(
                messages.getString("cooldown-active"),
                FormatManager.setTimeFormat(Math.round((float) (nextUse - now) / 1000.0)),
                commandName
                )
        );
    }
}
