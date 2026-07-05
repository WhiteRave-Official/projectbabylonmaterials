package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.init.PBAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class LifestealHandler {
    private LifestealHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0.0F || event.getEntity().level().isClientSide) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        double lifesteal = getAttributeValueSafely(attacker, PBAttributes.LIFESTEAL.get());
        if (lifesteal <= 0.0D) {
            return;
        }

        float healAmount = (float) (event.getAmount() * lifesteal);
        if (healAmount > 0.0F) {
            attacker.heal(healAmount);
        }
    }

    private static double getAttributeValueSafely(LivingEntity entity, Attribute attribute) {
        try {
            return entity.getAttributeValue(attribute);
        } catch (IllegalArgumentException ignored) {
            return 0.0D;
        }
    }
}
