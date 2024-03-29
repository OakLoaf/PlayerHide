package me.dave.playerhide.hook;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
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
import me.dave.platyutils.hook.Hook;
import me.dave.playerhide.PlayerHide;
import me.dave.playerhide.visibility.VisibilityState;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.meta.Metadata;
import me.tofaa.entitylib.meta.types.PlayerMeta;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PacketEventsHook extends Hook {
    private static final ConcurrentHashMap<UUID, PlayerMeta> playerMetaCache = new ConcurrentHashMap<>();

    public PacketEventsHook() {
        super(HookId.PACKET_EVENTS.toString());

        PacketEventsAPI<?> api = PacketEvents.getAPI();
        api.getEventManager().registerListener(new PacketEventsListener());

        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(PlayerHide.getInstance());
        APIConfig settings = new APIConfig(PacketEvents.getAPI())
            .trackPlatformEntities()
            .usePlatformLogger();

        EntityLib.init(platform, settings);
    }

    public void setInvisible(Player viewer, Player player) {
        setInvisible(List.of(viewer), player);
    }

    public void setInvisible(Collection<? extends Player> viewers, Player player) {
        PlayerMeta playerMeta = convertBukkitPlayer(player);
        playerMeta.setInvisible(true);
        viewers.forEach(viewer -> PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, playerMeta.createPacket()));
    }

    public void syncPlayerFlags(Player viewer, Player player) {
        syncPlayerFlags(List.of(viewer), player);
    }

    public void syncPlayerFlags(Collection<? extends Player> viewers, Player player) {
        PlayerMeta playerMeta = convertBukkitPlayer(player);
        viewers.forEach(viewer -> PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, playerMeta.createPacket()));
    }

    public void showBootsOnly(Player viewer, Player player) {
        showBootsOnly(List.of(viewer), player);
    }

    public void showBootsOnly(Collection<? extends Player> viewers, Player player) {
        int entityId = player.getEntityId();
        List<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment(EquipmentSlot.HELMET, new ItemStack.Builder().type(ItemTypes.AIR).build()));
        equipment.add(new Equipment(EquipmentSlot.CHEST_PLATE, new ItemStack.Builder().type(ItemTypes.AIR).build()));
        equipment.add(new Equipment(EquipmentSlot.LEGGINGS, new ItemStack.Builder().type(ItemTypes.AIR).build()));
        equipment.add(new Equipment(EquipmentSlot.BOOTS, player.getInventory().getBoots() != null ? SpigotConversionUtil.fromBukkitItemStack(player.getInventory().getBoots()) : new ItemStack.Builder().type(ItemTypes.GOLDEN_BOOTS).build()));
        equipment.add(new Equipment(EquipmentSlot.MAIN_HAND, new ItemStack.Builder().type(ItemTypes.AIR).build()));
        equipment.add(new Equipment(EquipmentSlot.OFF_HAND, new ItemStack.Builder().type(ItemTypes.AIR).build()));

        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(entityId, equipment);
        viewers.forEach(viewer -> PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, packet));
    }

    public void syncEquipmentPackets(Player viewer, Player player) {
        syncEquipmentPackets(List.of(viewer), player);
    }

    public void syncEquipmentPackets(Collection<? extends Player> viewers, Player player) {
        int entityId = player.getEntityId();
        List<Equipment> equipment = new ArrayList<>();
        PlayerInventory inventory = player.getInventory();
        equipment.add(new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(inventory.getHelmet())));
        equipment.add(new Equipment(EquipmentSlot.CHEST_PLATE, SpigotConversionUtil.fromBukkitItemStack(inventory.getChestplate())));
        equipment.add(new Equipment(EquipmentSlot.LEGGINGS, SpigotConversionUtil.fromBukkitItemStack(inventory.getLeggings())));
        equipment.add(new Equipment(EquipmentSlot.BOOTS, SpigotConversionUtil.fromBukkitItemStack(inventory.getBoots())));
        equipment.add(new Equipment(EquipmentSlot.MAIN_HAND, SpigotConversionUtil.fromBukkitItemStack(inventory.getItemInMainHand())));
        equipment.add(new Equipment(EquipmentSlot.OFF_HAND, SpigotConversionUtil.fromBukkitItemStack(inventory.getItemInOffHand())));

        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(entityId, equipment);
        viewers.forEach(viewer -> PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, packet));
    }

    public static PlayerMeta convertBukkitPlayer(Player player) {
        PlayerMeta playerMeta;
        if (playerMetaCache.containsKey(player.getUniqueId())) {
            playerMeta = playerMetaCache.get(player.getUniqueId());
        } else {
            playerMeta = new PlayerMeta(player.getEntityId(), new Metadata(player.getEntityId()));
            playerMetaCache.put(player.getUniqueId(), playerMeta);
        }

        playerMeta.setOnFire(player.getFireTicks() > 0);
        playerMeta.setSneaking(player.isSneaking());
        playerMeta.setSprinting(player.isSprinting());
        playerMeta.setSwimming(player.isSwimming());
        playerMeta.setInvisible(player.isInvisible());
        playerMeta.setHasGlowingEffect(player.isGlowing());
        playerMeta.setFlyingWithElytra(player.isGliding());

        return playerMeta;
    }

    @Nullable
    private static Player findPlayerByEntityId(int entityId) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getEntityId() == entityId) {
                return player;
            }
        }

        return null;
    }

    public static class PacketEventsListener extends PacketListenerAbstract {

        @Override
        public void onPacketSend(PacketSendEvent event) {
            if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
                WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);

                Player player = findPlayerByEntityId(packet.getEntityId());
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

                Player player = findPlayerByEntityId(packet.getEntityId());
                if (player != null && player.getUniqueId() != event.getUser().getUUID()) {
                    if (PlayerHide.getInstance().getVisibilityManager().getState(player.getUniqueId()).equals(VisibilityState.BOOTS_ONLY)) {
                        PlayerMeta playerMeta = convertBukkitPlayer(player);
                        playerMeta.setInvisible(true);
                        packet.setEntityMetadata(playerMeta);
                    }
                }
            }
        }
    }
}
