package org.lushplugins.playerhide.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.Metadata;
import me.tofaa.entitylib.meta.types.PlayerMeta;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PacketEventsHelper {

    public static void setInvisible(Collection<? extends Player> viewers, Player player) {
        int entityId = player.getEntityId();
        // Hacky method to get a PlayerMeta object with the player's current metadata
        Metadata metadata = new Metadata(entityId);
        metadata.setMetaFromPacket(new WrapperPlayServerEntityMetadata(player.getEntityId(), SpigotConversionUtil.getEntityMetadata(player)));

        PlayerMeta playerMeta = new PlayerMeta(entityId, metadata);
        playerMeta.setInvisible(true);

        viewers.forEach(viewer -> PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, playerMeta.createPacket()));
    }

    public static void syncPlayerFlags(Collection<? extends Player> viewers, Player player) {
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(player.getEntityId(), SpigotConversionUtil.getEntityMetadata(player));
        viewers.forEach(viewer -> PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, packet));
    }

    public static List<Equipment> getEquipmentWithBootsOnly(Player player) {
        ItemStack empty = new ItemStack.Builder().type(ItemTypes.AIR).build();
        org.bukkit.inventory.ItemStack playerBoots = player.getInventory().getBoots();

        ItemStack boots;
        if (playerBoots != null && !playerBoots.isEmpty()) {
            boots = SpigotConversionUtil.fromBukkitItemStack(playerBoots);
        } else {
            boots = new ItemStack.Builder().type(ItemTypes.GOLDEN_BOOTS).build();
        }

        return List.of(
            new Equipment(EquipmentSlot.HELMET, empty),
            new Equipment(EquipmentSlot.CHEST_PLATE, empty),
            new Equipment(EquipmentSlot.LEGGINGS, empty),
            new Equipment(EquipmentSlot.BOOTS, boots),
            new Equipment(EquipmentSlot.MAIN_HAND, empty),
            new Equipment(EquipmentSlot.OFF_HAND, empty)
        );
    }

    public static void showBootsOnly(Collection<? extends Player> viewers, Player player) {
        int entityId = player.getEntityId();
        List<Equipment> equipment = getEquipmentWithBootsOnly(player);

        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(entityId, equipment);
        viewers.forEach(viewer -> PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, packet));
    }

    public static void syncEquipmentPackets(Collection<? extends Player> viewers, Player player) {
        int entityId = player.getEntityId();
        PlayerInventory inventory = player.getInventory();
        List<Equipment> equipment = List.of(
            new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(inventory.getHelmet())),
            new Equipment(EquipmentSlot.CHEST_PLATE, SpigotConversionUtil.fromBukkitItemStack(inventory.getChestplate())),
            new Equipment(EquipmentSlot.LEGGINGS, SpigotConversionUtil.fromBukkitItemStack(inventory.getLeggings())),
            new Equipment(EquipmentSlot.BOOTS, SpigotConversionUtil.fromBukkitItemStack(inventory.getBoots())),
            new Equipment(EquipmentSlot.MAIN_HAND, SpigotConversionUtil.fromBukkitItemStack(inventory.getItemInMainHand())),
            new Equipment(EquipmentSlot.OFF_HAND, SpigotConversionUtil.fromBukkitItemStack(inventory.getItemInOffHand()))
        );

        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(entityId, equipment);
        viewers.forEach(viewer -> PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, packet));
    }

    public static @Nullable Player findPlayerByEntityId(int entityId) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getEntityId() == entityId) {
                return player;
            }
        }

        return null;
    }
}
