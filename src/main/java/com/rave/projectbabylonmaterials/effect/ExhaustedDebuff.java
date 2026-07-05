package com.rave.projectbabylonmaterials.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class ExhaustedDebuff extends MobEffect {
    private static final UUID MAX_STAMINA_REDUCTION_UUID = UUID.fromString("4d8398ef-562b-47ca-b1dd-c224d21a5e4a");
    private static final UUID STAMINA_REGEN_REDUCTION_UUID = UUID.fromString("3f6170f4-834d-4940-a84b-0f52d9110c84");
    private static final String MAX_STAMINA_ATTRIBUTE_ID = "epicfight:max_stamina";
    private static final String STAMINA_REGEN_ATTRIBUTE_ID = "epicfight:stamina_regen";

    public ExhaustedDebuff() {
        super(MobEffectCategory.HARMFUL, 0x8C7C6A);
        EffectAttributeCompat.addOptionalAttributeModifier(this, MAX_STAMINA_ATTRIBUTE_ID, MAX_STAMINA_REDUCTION_UUID.toString(), -0.15D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        EffectAttributeCompat.addOptionalAttributeModifier(this, STAMINA_REGEN_ATTRIBUTE_ID, STAMINA_REGEN_REDUCTION_UUID.toString(), -0.15D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
