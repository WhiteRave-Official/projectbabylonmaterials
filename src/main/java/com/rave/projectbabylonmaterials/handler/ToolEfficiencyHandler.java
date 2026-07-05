package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.init.PBAttributes;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ToolEfficiencyHandler {
    private ToolEfficiencyHandler() {
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.isCanceled() || event.getNewSpeed() <= 0.0F) {
            return;
        }

        double toolEfficiency = event.getEntity().getAttributeValue(PBAttributes.TOOL_EFFICIENCY.get());
        if (toolEfficiency <= 0.0D) {
            return;
        }

        event.setNewSpeed((float) (event.getNewSpeed() * (1.0D + toolEfficiency)));
    }
}
