package com.rave.projectbabylonmaterials.network.client;

import com.rave.projectbabylonmaterials.client.photon.PBMPhotonEffectHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundArmorPhotonEffectPacket(int entityId, String effectId, boolean active, float radius) {
    public static final String ICE_AURA = "armor_ice_aura";
    public static final String NETHERITE_FIRE_RING = "armor_netherite_fire_ring";
    public static final String DRAGONSTEEL_REBIRTH = "armor_dragonsteel_rebirth";

    public static void encode(ClientboundArmorPhotonEffectPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.entityId);
        buffer.writeUtf(packet.effectId);
        buffer.writeBoolean(packet.active);
        buffer.writeFloat(packet.radius);
    }

    public static ClientboundArmorPhotonEffectPacket decode(FriendlyByteBuf buffer) {
        return new ClientboundArmorPhotonEffectPacket(buffer.readVarInt(), buffer.readUtf(), buffer.readBoolean(), buffer.readFloat());
    }

    public static void handle(ClientboundArmorPhotonEffectPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null) {
                return;
            }

            Entity entity = minecraft.level.getEntity(packet.entityId());
            if (entity == null) {
                return;
            }

            switch (packet.effectId()) {
                case ICE_AURA -> {
                    if (packet.active()) {
                        PBMPhotonEffectHelper.startArmorIceAura(entity, packet.radius());
                    } else {
                        PBMPhotonEffectHelper.stopArmorIceAura(entity);
                    }
                }
                case NETHERITE_FIRE_RING -> {
                    if (packet.active()) {
                        PBMPhotonEffectHelper.startArmorNetheriteFireRing(entity, packet.radius());
                    } else {
                        PBMPhotonEffectHelper.stopArmorNetheriteFireRing(entity);
                    }
                }
                case DRAGONSTEEL_REBIRTH -> PBMPhotonEffectHelper.spawnArmorDragonsteelRebirth(entity);
                default -> {
                }
            }
        });
        context.setPacketHandled(true);
    }
}
