package org.lushplugins.playerhide.listeners;

import org.bukkit.event.player.PlayerQuitEvent;
import org.lushplugins.playerhide.PlayerHide;
import org.lushplugins.playerhide.util.PacketEventsHelper;
import org.lushplugins.playerhide.visibility.VisibilityManager;
import org.lushplugins.playerhide.visibility.VisibilityState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        VisibilityManager visibilityManager = PlayerHide.getInstance().getVisibilityManager();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (visibilityManager.getState(onlinePlayer.getUniqueId()) == VisibilityState.HIDDEN) {
                player.hidePlayer(PlayerHide.getInstance(), onlinePlayer);
            }
        }

        visibilityManager.calculateAndUpdateState(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        PacketEventsHelper.removeCachedMeta(uuid);
        PlayerHide.getInstance().getVisibilityManager().removeCachedUser(uuid);
    }
}
