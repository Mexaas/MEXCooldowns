package org.axc.mexcooldowns.MiniMessage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

public class TextFormat {
    public static final MiniMessage MM = MiniMessage.miniMessage();
    public static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    public static Component parse(String text) {
        if (text.contains("<") && text.contains(">")) {
            return MM.deserialize(text);
        } else {
            return LEGACY.deserialize(text);
        }
    }
    public static Component parseHolders(String text, long value, String command) {
        String parsed = text.replace("%time%", String.valueOf(value)).replace("%command%", command);
        if (text.contains("<") && text.contains(">")) {
            return MM.deserialize(parsed);
        }
        return LEGACY.deserialize(parsed);
    }
    public static Component parseHolders(String text, String value, String command) {
        String parsed = text.replace("%time%", String.valueOf(value)).replace("%command%", command);
        if (text.contains("<") && text.contains(">")) {
            return MM.deserialize(parsed);
        }
        return LEGACY.deserialize(parsed);
    }

    public static Component parseLegacy(String text) {
        return LEGACY.deserialize(text);
    }
    public static Component parseMini(String text) {
        String version = Bukkit.getBukkitVersion();
        if (version.startsWith("1.18")) {

        }


        return MM.deserialize(text);
    }
}
