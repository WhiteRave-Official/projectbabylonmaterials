package com.rave.projectbabylonmaterials.network.client;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.client.overlay.DragonsteelCooldownClientState;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundDragonsteelCooldownPacket(int remainingTicks) implements CustomPacketPayload {

    public static final Type<ClientboundDragonsteelCooldownPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "dragonsteel_cooldown"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDragonsteelCooldownPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ClientboundDragonsteelCooldownPacket::remainingTicks,
                    ClientboundDragonsteelCooldownPacket::new
            );

    @Override
    public Type<ClientboundDragonsteelCooldownPacket> type() {
        return TYPE;
    }

    public static void handle(ClientboundDragonsteelCooldownPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> DragonsteelCooldownClientState.setRemainingTicks(packet.remainingTicks()));
    }
}
