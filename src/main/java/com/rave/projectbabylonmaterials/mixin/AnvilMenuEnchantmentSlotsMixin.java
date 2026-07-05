package com.rave.projectbabylonmaterials.mixin;

import com.rave.projectbabylonmaterials.enchantment.EnchantmentSlotHelper;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuEnchantmentSlotsMixin {
    @Inject(method = "createResult", at = @At("TAIL"))
    private void pbm$enforceEnchantmentSlots(CallbackInfo callbackInfo) {
        AnvilMenu menu = (AnvilMenu) (Object) this;
        ItemStack input = menu.getSlot(0).getItem();
        ItemStack result = menu.getSlot(2).getItem();
        if (result.isEmpty() || !EnchantmentSlotHelper.wouldExceedSlotLimit(result)) {
            return;
        }

        menu.getSlot(2).set(ItemStack.EMPTY);
        if (EnchantmentSlotHelper.shouldBlockEnchantingTable(input)) {
            menu.setMaximumCost(EnchantmentSlotHelper.NO_ENCHANTMENT_SLOTS_COST);
        } else {
            menu.setMaximumCost(EnchantmentSlotHelper.NO_AVAILABLE_ENCHANTMENT_SLOTS_COST);
        }
    }
}