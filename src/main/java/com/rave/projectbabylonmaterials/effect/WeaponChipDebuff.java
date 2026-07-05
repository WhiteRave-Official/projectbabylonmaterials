package com.rave.projectbabylonmaterials.effect;

import java.util.UUID;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class WeaponChipDebuff extends MobEffect {
    private static final UUID IMPACT_REDUCTION_UUID = UUID.fromString("b5ef8de5-4a54-4b5c-80b6-8ef5ff0a3a77");
    private static final String IMPACT_ATTRIBUTE_ID = "epicfight:impact";

    public WeaponChipDebuff() {
        super(MobEffectCategory.HARMFUL, 0x7A2D2D);
        EffectAttributeCompat.addOptionalAttributeModifier(this, IMPACT_ATTRIBUTE_ID, IMPACT_REDUCTION_UUID.toString(), -0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
