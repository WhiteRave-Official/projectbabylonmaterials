package com.rave.projectbabylonmaterials.mixin;

import com.rave.projectbabylonmaterials.enchantment.EnchantmentSlotHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackEnchantableMixin {
    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void pbm$blockZeroSlotEnchanting(CallbackInfoReturnable<Boolean> callbackInfo) {
        ItemStack stack = (ItemStack) (Object) this;
        if (EnchantmentSlotHelper.shouldBlockEnchantingTable(stack)) {
            callbackInfo.setReturnValue(false);
        }
    }
}