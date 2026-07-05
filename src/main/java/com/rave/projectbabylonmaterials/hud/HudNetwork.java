package com.rave.projectbabylonmaterials.hud;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * A network channel dedicated to the HUD layout, deliberately SEPARATE from {@code PBNetwork} (which carries
 * gem/combat packets). Registered via its own {@code modBus.addListener(HudNetwork::register)} so that, when
 * the gem features are split into another mod, this HUD module and its channel move as one self-contained unit.
 */
public final class HudNetwork {
    public static final String CHANNEL = "project_babylon_materials_hud";
    private static final String PROTOCOL_VERSION = "1";

    private HudNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(CHANNEL).versioned(PROTOCOL_VERSION);
        registrar.playToClient(
                ClientboundHudLayoutPacket.TYPE,
                ClientboundHudLayoutPacket.STREAM_CODEC,
                ClientboundHudLayoutPacket::handle
        );
    }
}
