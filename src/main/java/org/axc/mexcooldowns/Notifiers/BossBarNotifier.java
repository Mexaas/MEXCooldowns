package org.axc.mexcooldowns.Notifiers;

import org.bukkit.entity.Player;

public class BossBarNotifier implements CooldownNotifier {
    @Override
    public void sendNotification(Player user, long nextUse, String commandName) {
        System.out.println("BossBar execution");
    }
}
