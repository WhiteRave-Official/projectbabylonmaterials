package com.rave.projectbabylonmaterials.effect;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class UnstableDebuff extends MobEffect {
    private static final ResourceLocation SPELL_RESIST_ATTRIBUTE_ID = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "spell_resist");
    private static final ResourceLocation SPELL_RESIST_REDUCTION_ID = ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "unstable_spell_resist_reduction");
    private static final double SPELL_RESIST_REDUCTION_PER_LEVEL = -0.15D;

    public UnstableDebuff() {
        super(MobEffectCategory.HARMFUL, 0xD6AF2C);
    }

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);
        applySpellResistReduction(attributeMap, amplifier);
    }

    @Override
    public void removeAttributeModifiers(AttributeMap attributeMap) {
        removeSpellResistReduction(attributeMap);
        super.removeAttributeModifiers(attributeMap);
    }

    private static void applySpellResistReduction(AttributeMap attributeMap, int amplifier) {
        AttributeInstance spellResist = resolveSpellResistAttribute(attributeMap);
        if (spellResist == null) {
            return;
        }
        if (spellResist.getModifier(SPELL_RESIST_REDUCTION_ID) != null) {
            spellResist.removeModifier(SPELL_RESIST_REDUCTION_ID);
        }
        spellResist.addTransientModifier(new AttributeModifier(
                SPELL_RESIST_REDUCTION_ID,
                SPELL_RESIST_REDUCTION_PER_LEVEL * (amplifier + 1),
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        ));
    }

    private static void removeSpellResistReduction(AttributeMap attributeMap) {
        AttributeInstance spellResist = resolveSpellResistAttribute(attributeMap);
        if (spellResist != null && spellResist.getModifier(SPELL_RESIST_REDUCTION_ID) != null) {
            spellResist.removeModifier(SPELL_RESIST_REDUCTION_ID);
        }
    }

    private static AttributeInstance resolveSpellResistAttribute(AttributeMap attributeMap) {
        return BuiltInRegistries.ATTRIBUTE.getHolder(SPELL_RESIST_ATTRIBUTE_ID)
                .map(attributeMap::getInstance)
                .orElse(null);
    }
}
