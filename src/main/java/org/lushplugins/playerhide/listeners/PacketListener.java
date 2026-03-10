package org.lushplugins.playerhide.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.types.PlayerMeta;
import org.bukkit.entity.Player;
import org.lushplugins.playerhide.PlayerHide;
import org.lushplugins.playerhide.util.PacketEventsHelper;
import org.lushplugins.playerhide.visibility.VisibilityState;

import java.util.ArrayList;
import java.util.List;

public class PacketListener extends PacketListenerAbstract {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);

            Player player = PacketEventsHelper.findPlayerByEntityId(packet.getEntityId());
            if (player != null && player.getUniqueId() != event.getUser().getUUID()) {
                if (PlayerHide.getInstance().getVisibilityManager().getState(player.getUniqueId()).equals(VisibilityState.BOOTS_ONLY)) {
                    List<Equipment> equipment = new ArrayList<>();
                    equipment.add(new Equipment(EquipmentSlot.HELMET, new ItemStack.Builder().type(ItemTypes.AIR).build()));
                    equipment.add(new Equipment(EquipmentSlot.CHEST_PLATE, new ItemStack.Builder().type(ItemTypes.AIR).build()));
                    equipment.add(new Equipment(EquipmentSlot.LEGGINGS, new ItemStack.Builder().type(ItemTypes.AIR).build()));
                    equipment.add(new Equipment(EquipmentSlot.BOOTS, player.getInventory().getBoots() != null ? SpigotConversionUtil.fromBukkitItemStack(player.getInventory().getBoots()) : new ItemStack.Builder().type(ItemTypes.GOLDEN_BOOTS).build()));
                    equipment.add(new Equipment(EquipmentSlot.MAIN_HAND, new ItemStack.Builder().type(ItemTypes.AIR).build()));
                    equipment.add(new Equipment(EquipmentSlot.OFF_HAND, new ItemStack.Builder().type(ItemTypes.AIR).build()));

                    packet.setEquipment(equipment);
                }
            }
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);

            Player player = PacketEventsHelper.findPlayerByEntityId(packet.getEntityId());
            if (player != null && player.getUniqueId() != event.getUser().getUUID()) {
                if (PlayerHide.getInstance().getVisibilityManager().getState(player.getUniqueId()).equals(VisibilityState.BOOTS_ONLY)) {
                    PlayerMeta playerMeta = PacketEventsHelper.convertBukkitPlayer(player);
                    playerMeta.setInvisible(true);
                    packet.setEntityMetadata(playerMeta);
                }
            }
        }
    }
}
