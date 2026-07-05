package com.rave.projectbabylonmaterials.network.client;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundShadowFormStatePacket(int entityId, boolean concealed) implements CustomPacketPayload {

    public static final Type<ClientboundShadowFormStatePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "shadow_form_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundShadowFormStatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ClientboundShadowFormStatePacket::entityId,
                    ByteBufCodecs.BOOL, ClientboundShadowFormStatePacket::concealed,
                    ClientboundShadowFormStatePacket::new
            );

    @Override
    public Type<ClientboundShadowFormStatePacket> type() {
        return TYPE;
    }

    public static void handle(ClientboundShadowFormStatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level == null) {
                return;
            }
            Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId());
            if (entity != null) {
                entity.setInvisible(packet.concealed());
            }
        });
    }
}
