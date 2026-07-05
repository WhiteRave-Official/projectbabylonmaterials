package com.rave.projectbabylonmaterials.balance;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

/**
 * Rebuilds the {@link PBMBalances} snapshot on the client once the synced datapack registries arrive at login,
 * so client-only consumers (tooltips, station screens) see the same values as the server.
 */
@EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, value = Dist.CLIENT)
public final class PBMClientBalanceSync {
    private PBMClientBalanceSync() {
    }

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            PBMBalances.rebuild(connection.registryAccess());
        }
    }
}
