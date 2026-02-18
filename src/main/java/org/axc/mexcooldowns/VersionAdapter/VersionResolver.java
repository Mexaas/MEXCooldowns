package org.axc.mexcooldowns.VersionAdapter;

import org.axc.mexcooldowns.Versions.V1_16_5;
import org.axc.mexcooldowns.Versions.V1_18_PLUS;
import org.bukkit.Bukkit;

public class VersionResolver {
    private static VersionAdapter adapter;
    private static final String serverVersion = Bukkit.getBukkitVersion().split("-")[0];

    public static void initVersion() {
        if (serverVersion.contains("1.16") || serverVersion.contains("1.17")) {
            adapter = new V1_16_5();
        } if (serverVersion.contains("1.18") || serverVersion.contains("1.19")
                || serverVersion.contains("1.20") || serverVersion.contains("1.21")) {
            adapter = new V1_18_PLUS();
        }
    }
    public static VersionAdapter getAdapter() {
        return adapter;
    }
}