package org.axc.mexcooldowns.Backend;

import org.axc.mexcooldowns.Mexcooldowns;
import org.bukkit.configuration.file.FileConfiguration;

public class FormatManager {
    public static String setTimeFormat(long seconds) {
        FileConfiguration config = Mexcooldowns.getInstance().getConfig();
        String s = config.getString("format.seconds", "seconds"), m = config.getString("format.minutes", "minutes");
        String h = config.getString("format.hours", "hours"), d = config.getString("format.days", "days");
        StringBuilder sb = new StringBuilder();

        long days = seconds / 86400L; seconds %= 86400L;
        long hours = seconds / 3600L; seconds %= 3600L;
        long minutes = seconds / 60L; seconds %= 60L;

        if (days > 0L) {
            sb.append(days).append(" ").append(d).append(" ");
        }
        if (hours > 0L) {
            sb.append(hours).append(" ").append(h).append(" ");
        }
        if (minutes > 0L) {
            sb.append(minutes).append(" ").append(m).append(" ");
        }
        if (seconds > 0L || sb.isEmpty()) {
            sb.append(seconds).append(" ").append(s);
        }

        return sb.toString().trim();
    }
}
