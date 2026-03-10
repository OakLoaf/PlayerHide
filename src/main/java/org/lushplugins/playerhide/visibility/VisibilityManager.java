package org.lushplugins.playerhide.visibility;

import org.lushplugins.playerhide.PlayerHide;
import org.lushplugins.playerhide.util.PacketEventsHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.lushplugins.playerhide.util.WorldGuardHelper;

import java.util.*;

public class VisibilityManager {
    private final HashMap<UUID, VisibilityState> visibilityCache;

    public VisibilityManager() {
        this.visibilityCache = new HashMap<>();
    }

    public VisibilityState getState(UUID uuid) {
        return visibilityCache.getOrDefault(uuid, VisibilityState.SHOWN);
    }

    public void updateState(Player player, VisibilityState newState) {
        UUID uuid = player.getUniqueId();
        VisibilityState oldState = getState(uuid);
        visibilityCache.put(uuid, newState);
        if (newState == oldState) {
            return;
        }

        switch (oldState) {
            case BOOTS_ONLY -> {
                List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                onlinePlayers.remove(player);

                PacketEventsHelper.syncPlayerFlags(onlinePlayers, player);
                PacketEventsHelper.syncEquipmentPackets(onlinePlayers, player);
            }
            case HIDDEN -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer != player) {
                        onlinePlayer.showPlayer(PlayerHide.getInstance(), player);
                    }
                }
            }
        }

        switch (newState) {
            case BOOTS_ONLY -> {
                Collection<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                onlinePlayers.remove(player);

                PacketEventsHelper.setInvisible(onlinePlayers, player);
                PacketEventsHelper.showBootsOnly(onlinePlayers, player);
            }
            case HIDDEN -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer != player) {
                        onlinePlayer.hidePlayer(PlayerHide.getInstance(), player);
                    }
                }
            }
        }
    }

    public VisibilityState calculateState(Player player) {
        if (WorldGuardHelper.shouldHide(player)) {
            return VisibilityState.HIDDEN;
        } else if (WorldGuardHelper.shouldShowBootsOnly(player)) {
            return VisibilityState.BOOTS_ONLY;
        } else {
            return VisibilityState.SHOWN;
        }
    }

    public VisibilityState calculateAndUpdateState(Player player) {
        VisibilityState state = calculateState(player);
        updateState(player, state);
        return state;
    }

    public void removeCachedUser(UUID uuid) {
        visibilityCache.remove(uuid);
    }
}
