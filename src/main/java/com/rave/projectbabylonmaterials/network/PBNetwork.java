package com.rave.projectbabylonmaterials.network;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.network.client.ClientboundCritEffectPacket;
import com.rave.projectbabylonmaterials.network.client.ClientboundDragonsteelCooldownPacket;
import com.rave.projectbabylonmaterials.network.client.ClientboundShadowFormAfterimagePacket;
import com.rave.projectbabylonmaterials.network.client.ClientboundShadowFormStatePacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class PBNetwork {

    private static final String PROTOCOL_VERSION = "1";

    private PBNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(ProjectBabylonMaterials.MODID).versioned(PROTOCOL_VERSION);

        registrar.playToClient(
                ClientboundCritEffectPacket.TYPE,
                ClientboundCritEffectPacket.STREAM_CODEC,
                ClientboundCritEffectPacket::handle
        );
        registrar.playToClient(
                ClientboundDragonsteelCooldownPacket.TYPE,
                ClientboundDragonsteelCooldownPacket.STREAM_CODEC,
                ClientboundDragonsteelCooldownPacket::handle
        );
        // Shadow-form packets are purely cosmetic S2C visuals: register them optional so a
        // client running an older Materials build can still join the server.
        PayloadRegistrar optionalRegistrar = registrar.optional();
        optionalRegistrar.playToClient(
                ClientboundShadowFormStatePacket.TYPE,
                ClientboundShadowFormStatePacket.STREAM_CODEC,
                ClientboundShadowFormStatePacket::handle
        );
        optionalRegistrar.playToClient(
                ClientboundShadowFormAfterimagePacket.TYPE,
                ClientboundShadowFormAfterimagePacket.STREAM_CODEC,
                ClientboundShadowFormAfterimagePacket::handle
        );
    }
}
