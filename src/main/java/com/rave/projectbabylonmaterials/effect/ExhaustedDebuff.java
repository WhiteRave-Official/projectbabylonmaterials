package com.rave.projectbabylonmaterials.effect;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import yesman.epicfight.registry.entries.EpicFightAttributes;

public class ExhaustedDebuff extends MobEffect {
    private static final ResourceLocation MAX_STAMINA_REDUCTION_ID =
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "exhausted_max_stamina");
    private static final ResourceLocation STAMINA_REGEN_REDUCTION_ID =
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "exhausted_stamina_regen");

    public ExhaustedDebuff() {
        super(MobEffectCategory.HARMFUL, 0x8C7C6A);
        addAttributeModifier(EpicFightAttributes.MAX_STAMINA, MAX_STAMINA_REDUCTION_ID, -0.15D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(EpicFightAttributes.STAMINA_REGEN, STAMINA_REGEN_REDUCTION_ID, -0.15D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
