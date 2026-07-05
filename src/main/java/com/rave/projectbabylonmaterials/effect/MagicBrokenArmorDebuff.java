package com.rave.projectbabylonmaterials.effect;

import java.util.UUID;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class MagicBrokenArmorDebuff extends MobEffect {
    private static final UUID SPELL_RESIST_REDUCTION_UUID = UUID.fromString("7a0d5a5e-0c3f-4a56-9bb9-84f7a2b7b8d4");
    private static final String SPELL_RESIST_ATTRIBUTE_ID = "irons_spellbooks:spell_resist";

    public MagicBrokenArmorDebuff() {
        super(MobEffectCategory.HARMFUL, 0x4A2F4A);
        EffectAttributeCompat.addOptionalAttributeModifier(this, SPELL_RESIST_ATTRIBUTE_ID, SPELL_RESIST_REDUCTION_UUID.toString(), -0.20D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}

