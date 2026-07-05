package com.rave.projectbabylonmaterials.effect;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import yesman.epicfight.registry.entries.EpicFightAttributes;

public class WeaponChipDebuff extends MobEffect {
    private static final ResourceLocation IMPACT_REDUCTION_ID =
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "weapon_chip_impact");

    public WeaponChipDebuff() {
        super(MobEffectCategory.HARMFUL, 0x7A2D2D);
        addAttributeModifier(EpicFightAttributes.IMPACT, IMPACT_REDUCTION_ID, -0.25D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
