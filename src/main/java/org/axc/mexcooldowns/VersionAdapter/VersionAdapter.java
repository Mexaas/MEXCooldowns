package org.axc.mexcooldowns.VersionAdapter;

import net.kyori.adventure.text.Component;

public interface VersionAdapter {
    Component parseHoldersMessage(String text);
    Component parseHoldersMessage(String text, String value, String command);
}