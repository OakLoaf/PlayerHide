package org.lushplugins.playerhide;

import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerHide extends JavaPlugin {
    private static PlayerHide plugin;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        // Enable implementation
    }

    @Override
    public void onDisable() {
        // Disable implementation
    }

    public static PlayerHide getInstance() {
        return plugin;
    }
}
