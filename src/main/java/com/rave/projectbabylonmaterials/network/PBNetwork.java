package com.rave.projectbabylonmaterials.network;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.network.client.ClientboundArmorPhotonEffectPacket;
import com.rave.projectbabylonmaterials.network.client.ClientboundCritEffectPacket;
import com.rave.projectbabylonmaterials.network.client.ClientboundDragonsteelCooldownPacket;
import com.rave.projectbabylonmaterials.network.client.ClientboundShadowFormAfterimagePacket;
import com.rave.projectbabylonmaterials.network.client.ClientboundShadowFormStatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PBNetwork {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId;

    private PBNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(
                packetId++,
                ClientboundCritEffectPacket.class,
                ClientboundCritEffectPacket::encode,
                ClientboundCritEffectPacket::decode,
                ClientboundCritEffectPacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                ClientboundDragonsteelCooldownPacket.class,
                ClientboundDragonsteelCooldownPacket::encode,
                ClientboundDragonsteelCooldownPacket::decode,
                ClientboundDragonsteelCooldownPacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                ClientboundShadowFormStatePacket.class,
                ClientboundShadowFormStatePacket::encode,
                ClientboundShadowFormStatePacket::decode,
                ClientboundShadowFormStatePacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                ClientboundShadowFormAfterimagePacket.class,
                ClientboundShadowFormAfterimagePacket::encode,
                ClientboundShadowFormAfterimagePacket::decode,
                ClientboundShadowFormAfterimagePacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                ClientboundArmorPhotonEffectPacket.class,
                ClientboundArmorPhotonEffectPacket::encode,
                ClientboundArmorPhotonEffectPacket::decode,
                ClientboundArmorPhotonEffectPacket::handle
        );
    }
}