package org.axc.mexcooldowns.Notifiers;

public class ActionBarNotifier implements CooldownNotifier {
    @Override
    public void sendNotification() {
        System.out.println("test");
    }
}
