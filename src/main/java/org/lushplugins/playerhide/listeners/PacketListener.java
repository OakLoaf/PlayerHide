package org.lushplugins.playerhide.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.tofaa.entitylib.meta.Metadata;
import me.tofaa.entitylib.meta.types.PlayerMeta;
import org.bukkit.entity.Player;
import org.lushplugins.playerhide.PlayerHide;
import org.lushplugins.playerhide.util.PacketEventsHelper;
import org.lushplugins.playerhide.visibility.VisibilityState;

public class PacketListener extends PacketListenerAbstract {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);

            Player player = PacketEventsHelper.findPlayerByEntityId(packet.getEntityId());
            if (player != null && player.getUniqueId() != event.getUser().getUUID()) {
                if (PlayerHide.getInstance().getVisibilityManager().getState(player.getUniqueId()).equals(VisibilityState.BOOTS_ONLY)) {
                    packet.setEquipment(PacketEventsHelper.getEquipmentWithBootsOnly(player));
                }
            }
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);

            Player player = PacketEventsHelper.findPlayerByEntityId(packet.getEntityId());
            if (player != null && player.getUniqueId() != event.getUser().getUUID()) {
                if (PlayerHide.getInstance().getVisibilityManager().getState(player.getUniqueId()).equals(VisibilityState.BOOTS_ONLY)) {
                    int entityId = player.getEntityId();
                    Metadata metadata = new Metadata(entityId);
                    metadata.setMetaFromPacket(packet);

                    PlayerMeta playerMeta = new PlayerMeta(entityId, metadata);
                    playerMeta.setInvisible(true);

                    packet.setEntityMetadata(playerMeta);
                }
            }
        }
    }
}
