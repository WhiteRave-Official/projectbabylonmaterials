package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.effect.BleedDamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProjectBabylonMaterials.MODID)
public final class BleedDamageEvents {
    private BleedDamageEvents() { }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void capture(LivingDamageEvent event) {
        if (event.getSource() instanceof BleedDamageSource source) source.capture(event);
    }
}
