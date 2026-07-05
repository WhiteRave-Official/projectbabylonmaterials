package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

public final class LivingEntityHealthHandler {
    private static final ResourceLocation UNIVERSAL_HEALTH_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "universal_living_health");
    private static final double UNIVERSAL_HEALTH_BONUS = 10.0D;

    private LivingEntityHealthHandler() {
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof LivingEntity livingEntity) || livingEntity instanceof ServerPlayer) {
            return;
        }

        applyUniversalHealthBonus(livingEntity);
    }

    public static void applyUniversalHealthBonus(LivingEntity entity) {
        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }

        double previousMaxHealth = entity.getMaxHealth();
        float previousHealth = entity.getHealth();
        if (maxHealth.getModifier(UNIVERSAL_HEALTH_MODIFIER_ID) != null) {
            maxHealth.removeModifier(UNIVERSAL_HEALTH_MODIFIER_ID);
        }

        maxHealth.addTransientModifier(new AttributeModifier(
                UNIVERSAL_HEALTH_MODIFIER_ID,
                UNIVERSAL_HEALTH_BONUS,
                AttributeModifier.Operation.ADD_VALUE
        ));

        double newMaxHealth = entity.getMaxHealth();
        if (Math.abs(newMaxHealth - previousMaxHealth) <= 0.0001D) {
            return;
        }

        float adjustedHealth = previousHealth;
        if (newMaxHealth > previousMaxHealth) {
            adjustedHealth = (float) Math.min(newMaxHealth, previousHealth + (newMaxHealth - previousMaxHealth));
        } else if (newMaxHealth < previousMaxHealth) {
            adjustedHealth = (float) Math.min(previousHealth, newMaxHealth);
        }

        entity.setHealth(adjustedHealth);
    }
}
