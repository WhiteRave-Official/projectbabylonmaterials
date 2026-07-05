package com.rave.projectbabylonmaterials.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

final class IronSpellbooksEffectCompat {
    private IronSpellbooksEffectCompat() {
    }

    static double getAttributeValue(LivingEntity entity, String attributeId, double fallback) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(attributeId));
        return attribute != null ? entity.getAttributeValue(attribute) : fallback;
    }

    static DamageSource createDamageSource(ServerLevel level, String damageTypeId) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(damageTypeId);
        if (resourceLocation == null) {
            return null;
        }

        return level.registryAccess()
                .registry(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                .flatMap(registry -> registry.getHolder(ResourceKeyHelper.createDamageTypeKey(resourceLocation)))
                .map(DamageSource::new)
                .orElse(null);
    }

    static boolean isDamageSource(DamageSource source, String damageTypeId) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(damageTypeId);
        return resourceLocation != null && source.is(ResourceKeyHelper.createDamageTypeKey(resourceLocation));
    }

    private static final class ResourceKeyHelper {
        private ResourceKeyHelper() {
        }

        private static net.minecraft.resources.ResourceKey<DamageType> createDamageTypeKey(ResourceLocation resourceLocation) {
            return net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE, resourceLocation);
        }
    }
}
