package com.rave.projectbabylonmaterials.mixin;

import com.rave.projectbabylonmaterials.handler.GemEffectHandler;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemDrawSpeedMixin {
    @Inject(method = "getChargeDuration", at = @At("RETURN"), cancellable = true)
    private static void pbm$adjustCrossbowChargeDuration(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(GemEffectHandler.adjustCrossbowChargeDuration(stack, cir.getReturnValue()));
    }
}
