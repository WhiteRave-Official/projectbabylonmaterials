package com.rave.projectbabylonmaterials.client.overlay;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class DragonsteelCooldownClientState {
    private static int remainingTicks;

    private DragonsteelCooldownClientState() {
    }

    public static int getRemainingTicks() {
        return remainingTicks;
    }

    public static void setRemainingTicks(int remainingTicks) {
        DragonsteelCooldownClientState.remainingTicks = Math.max(0, remainingTicks);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (remainingTicks > 0) {
            remainingTicks--;
        }
    }
}
