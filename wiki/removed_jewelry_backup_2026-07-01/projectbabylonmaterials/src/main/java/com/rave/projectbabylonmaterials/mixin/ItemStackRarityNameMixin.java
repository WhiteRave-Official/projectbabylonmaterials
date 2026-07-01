package com.rave.projectbabylonmaterials.mixin;

import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackRarityNameMixin {
    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    private void pbm$applyRarityName(CallbackInfoReturnable<Component> callbackInfo) {
        ItemStack stack = (ItemStack) (Object) this;
        callbackInfo.setReturnValue(ItemRarityHelper.createDisplayName(stack, callbackInfo.getReturnValue()));
    }
}