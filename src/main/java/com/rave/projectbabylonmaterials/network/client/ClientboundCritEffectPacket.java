package com.rave.projectbabylonmaterials.network.client;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.client.effect.CritEffectRenderer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundCritEffectPacket(double x, double y, double z) implements CustomPacketPayload {

    public static final Type<ClientboundCritEffectPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "crit_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCritEffectPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.DOUBLE, ClientboundCritEffectPacket::x,
                    ByteBufCodecs.DOUBLE, ClientboundCritEffectPacket::y,
                    ByteBufCodecs.DOUBLE, ClientboundCritEffectPacket::z,
                    ClientboundCritEffectPacket::new
            );

    @Override
    public Type<ClientboundCritEffectPacket> type() {
        return TYPE;
    }

    public static void handle(ClientboundCritEffectPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> CritEffectRenderer.spawn(packet.x(), packet.y(), packet.z()));
    }
}
