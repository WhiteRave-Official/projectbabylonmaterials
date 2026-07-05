package com.rave.projectbabylonmaterials.mixin;

import com.rave.projectbabylonmaterials.init.PBAttributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackDurabilityMixin {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void projectBabylonMaterials$preventToolDurabilityLoss(int amount, RandomSource random, ServerPlayer player,
                                                                    CallbackInfoReturnable<Boolean> cir) {
        if (amount <= 0 || player == null) {
            return;
        }

        double chance = player.getAttributeValue(PBAttributes.TOOL_DURABILITY.get());
        if (chance <= 0.0D) {
            return;
        }

        if (random.nextDouble() < Math.min(chance, 1.0D)) {
            cir.setReturnValue(false);
        }
    }
}
