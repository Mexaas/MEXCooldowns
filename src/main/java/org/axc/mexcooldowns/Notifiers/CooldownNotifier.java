package org.axc.mexcooldowns.Notifiers;

import org.bukkit.entity.Player;

public interface CooldownNotifier {
    void sendNotification(Player user, long nextUse, String commandName);
}
