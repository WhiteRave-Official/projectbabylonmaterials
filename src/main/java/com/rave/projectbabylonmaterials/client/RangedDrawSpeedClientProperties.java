package com.rave.projectbabylonmaterials.client;

import com.rave.projectbabylonmaterials.handler.RangedDrawSpeedHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public final class RangedDrawSpeedClientProperties {
    private RangedDrawSpeedClientProperties() {
    }

    public static float getBowPull(ItemStack stack, LivingEntity livingEntity) {
        if (livingEntity == null || !livingEntity.isUsingItem() || livingEntity.getUseItem() != stack) {
            return 0.0F;
        }

        int useTicks = stack.getUseDuration() - livingEntity.getUseItemRemainingTicks();
        return BowItem.getPowerForTime(RangedDrawSpeedHandler.adjustBowCharge(livingEntity, useTicks));
    }

    public static float getCrossbowPull(ItemStack stack, LivingEntity livingEntity) {
        if (livingEntity == null || !livingEntity.isUsingItem() || livingEntity.getUseItem() != stack || CrossbowItem.isCharged(stack)) {
            return 0.0F;
        }

        int useTicks = stack.getUseDuration() - livingEntity.getUseItemRemainingTicks();
        return RangedDrawSpeedHandler.getCrossbowChargeProgress(livingEntity, useTicks, CrossbowItem.getChargeDuration(stack));
    }
}
