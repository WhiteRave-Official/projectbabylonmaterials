package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.init.PBAttributes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public final class RangedDrawSpeedHandler {
    private RangedDrawSpeedHandler() {
    }

    public static int adjustBowCharge(LivingEntity livingEntity, int originalCharge) {
        double drawSpeedBonus = getDrawSpeedBonus(livingEntity);
        if (drawSpeedBonus <= 0.0D) {
            return originalCharge;
        }

        return Math.max(0, Mth.ceil(originalCharge * (1.0D + drawSpeedBonus)));
    }

    public static int adjustCrossbowChargeDuration(LivingEntity livingEntity, int originalDuration) {
        double drawSpeedBonus = getDrawSpeedBonus(livingEntity);
        if (drawSpeedBonus <= 0.0D) {
            return originalDuration;
        }

        return Math.max(1, Mth.floor(originalDuration / (1.0D + drawSpeedBonus)));
    }

    public static float getCrossbowChargeProgress(LivingEntity livingEntity, int usedTicks, int originalDuration) {
        int adjustedDuration = adjustCrossbowChargeDuration(livingEntity, originalDuration);
        return Mth.clamp((float) usedTicks / (float) adjustedDuration, 0.0F, 1.0F);
    }

    private static double getDrawSpeedBonus(LivingEntity livingEntity) {
        if (livingEntity == null) {
            return 0.0D;
        }

        return livingEntity.getAttributeValue(PBAttributes.RANGED_DRAW_SPEED.get());
    }
}
