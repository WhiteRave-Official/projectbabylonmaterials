package com.rave.projectbabylonmaterials.network.client;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundShadowFormAfterimagePacket(int entityId) implements CustomPacketPayload {

    public static final Type<ClientboundShadowFormAfterimagePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "shadow_form_afterimage"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundShadowFormAfterimagePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ClientboundShadowFormAfterimagePacket::entityId,
                    ClientboundShadowFormAfterimagePacket::new
            );

    @Override
    public Type<ClientboundShadowFormAfterimagePacket> type() {
        return TYPE;
    }

    public static void handle(ClientboundShadowFormAfterimagePacket packet, IPayloadContext context) {
        // Visual afterimage rendering is deferred (client shadow-render subsystem not ported).
        context.enqueueWork(() -> {
        });
    }
}
