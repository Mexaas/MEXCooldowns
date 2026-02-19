package org.axc.mexcooldowns.Notifiers;

public class ChatNotifier implements CooldownNotifier {
    @Override
    public void sendNotification() {
        System.out.println("test");
    }
}
