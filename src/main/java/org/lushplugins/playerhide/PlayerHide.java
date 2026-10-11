package org.lushplugins.playerhide;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;
import org.lushplugins.lushlib.utils.plugin.SpigotPlugin;
import org.lushplugins.playerhide.listeners.PacketListener;
import org.lushplugins.playerhide.util.WorldGuardHelper;
import org.lushplugins.playerhide.listeners.PlayerListener;
import org.lushplugins.playerhide.visibility.VisibilityManager;

public final class PlayerHide extends SpigotPlugin {
    private static PlayerHide plugin;

    private VisibilityManager visibilityManager;

    @Override
    public void onLoad() {
        plugin = this;

        WorldGuardHelper.registerFlags();
    }

    @Override
    public void onEnable() {
        registerListener(new PlayerListener());

        PacketEventsAPI<?> packetEvents = PacketEvents.getAPI();
        EntityLib.init(new APIConfig(
            packetEvents,
            new SpigotEntityLibPlatform(PlayerHide.getInstance()))
        );

        packetEvents.getEventManager().registerListener(new PacketListener());

        this.visibilityManager = new VisibilityManager();
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                PlayerHide.getInstance().getVisibilityManager().calculateAndUpdateState(player);
            });
        }, 0, 5);
    }

    public VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }

    public static PlayerHide getInstance() {
        return plugin;
    }
}
