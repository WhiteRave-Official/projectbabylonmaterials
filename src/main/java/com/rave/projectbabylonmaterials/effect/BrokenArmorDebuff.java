package com.rave.projectbabylonmaterials.effect;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BrokenArmorDebuff extends MobEffect {
    private static final ResourceLocation ARMOR_REDUCTION_ID =
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "broken_armor_reduction");

    public BrokenArmorDebuff() {
        super(MobEffectCategory.HARMFUL, 0x6B6B6B);
        addAttributeModifier(Attributes.ARMOR, ARMOR_REDUCTION_ID, -0.20D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
