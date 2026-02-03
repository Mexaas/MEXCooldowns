package org.axc.mexcooldowns.Versions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.axc.mexcooldowns.VersionAdapter.VersionAdapter;

public class V1_18_PLUS implements VersionAdapter {
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacyAmpersand();

    @Override
    public Component parseMessage(String text) {
        if (text.contains("<") && text.contains(">")) {
            return MM.deserialize(text);
        } else {
            return LEGACY.deserialize(text);
        }
    }
    @Override
    public Component parseHoldersMessage(String text, long value, String command) {
        String parsed = text.replace("%time%", String.valueOf(value)).replace("%command%", command);
        if (text.contains("<") && text.contains(">")) {
            return MM.deserialize(parsed);
        }
        return LEGACY.deserialize(parsed);
    }
    @Override
    public Component parseHoldersMessage(String text, String value, String command) {
        String parsed = text.replace("%time%", String.valueOf(value)).replace("%command%", command);
        if (text.contains("<") && text.contains(">")) {
            return MM.deserialize(parsed);
        }
        return LEGACY.deserialize(parsed);
    }
}
