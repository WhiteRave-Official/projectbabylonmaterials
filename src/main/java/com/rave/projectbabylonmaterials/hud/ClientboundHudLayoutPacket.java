package com.rave.projectbabylonmaterials.hud;

import com.rave.projectbabylonmaterials.client.hud.ClientHudLayoutState;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Syncs the datapack HUD layout to a client. Sent on its OWN network channel ({@link HudNetwork}),
 * independent of the gem/combat {@code PBNetwork}, so the whole HUD module can be lifted out later.
 */
public record ClientboundHudLayoutPacket(HudLayout layout) implements CustomPacketPayload {
    public static final Type<ClientboundHudLayoutPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HudNetwork.CHANNEL, "layout"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundHudLayoutPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodec(HudLayout.CODEC),
                    ClientboundHudLayoutPacket::layout,
                    ClientboundHudLayoutPacket::new
            );

    @Override
    public Type<ClientboundHudLayoutPacket> type() {
        return TYPE;
    }

    public static void handle(ClientboundHudLayoutPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientHudLayoutState.accept(packet.layout()));
    }
}
