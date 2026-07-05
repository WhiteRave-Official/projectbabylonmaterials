package com.rave.projectbabylonmaterials.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

final class EffectAttributeCompat {
    private EffectAttributeCompat() {
    }

    static void addOptionalAttributeModifier(MobEffect effect, String attributeId, String modifierId, double amount, AttributeModifier.Operation operation) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(attributeId));
        if (attribute != null) {
            effect.addAttributeModifier(attribute, modifierId, amount, operation);
        }
    }
}
