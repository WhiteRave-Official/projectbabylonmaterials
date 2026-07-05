package com.rave.projectbabylonmaterials.setbonus;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface ArmorSetBonus {

    ArmorSetBonusType getType();

    Component getDisplayName();

    ResourceLocation getFrameTexture();

    ResourceLocation getIconTexture();

    void apply(LivingEntity entity);

    void remove(LivingEntity entity);

    boolean isApplied(LivingEntity entity);

    List<Component> getTooltipLines();
}

