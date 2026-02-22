package org.axc.mexcooldowns.Versions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.axc.mexcooldowns.VersionAdapter.VersionAdapter;

public class V1_16_5 implements VersionAdapter {
    LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    @Override
    public Component parseHoldersMessage(String text) {
        return LEGACY.deserialize(text);
    }
    @Override
    public Component parseHoldersMessage(String text, String value, String command) {
        String parsed = text.replace("%time%", String.valueOf(value)).replace("%command%", command);
        return LEGACY.deserialize(parsed);
    }
}
