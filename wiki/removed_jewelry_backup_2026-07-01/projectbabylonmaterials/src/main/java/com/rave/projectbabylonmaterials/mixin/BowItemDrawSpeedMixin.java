package com.rave.projectbabylonmaterials.mixin;

import com.rave.projectbabylonmaterials.handler.GemEffectHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BowItem.class)
public abstract class BowItemDrawSpeedMixin {
    @ModifyArg(
            method = "releaseUsing",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BowItem;getPowerForTime(I)F"),
            index = 0
    )
    private int pbm$adjustBowCharge(int originalCharge, ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        return GemEffectHandler.adjustBowCharge(stack, originalCharge);
    }
}
