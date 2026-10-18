package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.effect.BleedOwnerData;
import com.rave.projectbabylonmaterials.effect.Bleeding;
import com.rave.projectbabylonmaterials.init.PBMEffects;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProjectBabylonMaterials.MODID)
public final class BleedOwnershipEvents {
    private BleedOwnershipEvents() { }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRemoved(MobEffectEvent.Remove event) {
        if (!event.getEntity().level().isClientSide() && event.getEffect() == PBMEffects.BLEED_DEBUFF.get()) {
            BleedOwnerData.clear(event.getEntity().getPersistentData());
            Bleeding.clearSchedule(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onExpired(MobEffectEvent.Expired event) {
        var effect = event.getEffectInstance();
        if (!event.getEntity().level().isClientSide() && effect != null && effect.getEffect() == PBMEffects.BLEED_DEBUFF.get()) {
            BleedOwnerData.clear(event.getEntity().getPersistentData());
            Bleeding.clearSchedule(event.getEntity());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDeath(LivingDeathEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            BleedOwnerData.clear(event.getEntity().getPersistentData());
            Bleeding.clearSchedule(event.getEntity());
        }
    }
}
