package com.rave.projectbabylonmaterials.effect;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ConcussedDebuff extends MobEffect {
    private static final ResourceLocation ATTACK_SPEED_REDUCTION_ID =
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "concussed_attack_speed");
    private static final ResourceLocation MOVEMENT_SPEED_REDUCTION_ID =
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "concussed_movement_speed");
    private static final ResourceLocation ATTACK_DAMAGE_REDUCTION_ID =
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "concussed_attack_damage");

    public ConcussedDebuff() {
        super(MobEffectCategory.HARMFUL, 0x8A6A4B);
        addAttributeModifier(Attributes.ATTACK_SPEED, ATTACK_SPEED_REDUCTION_ID, -0.30D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_REDUCTION_ID, -0.30D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_REDUCTION_ID, -0.30D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
