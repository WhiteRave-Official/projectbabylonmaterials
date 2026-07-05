package com.rave.projectbabylonmaterials.mixin;

import com.rave.projectbabylonmaterials.combat.ArmorCalculationHelper;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "yesman.epicfight.world.capabilities.item.ArmorCapability$Builder", remap = false)
public abstract class EpicFightArmorCapabilityBuilderMixin {
    @Shadow
    private double weight;

    @Shadow
    private double stunArmor;

    @Unique
    private boolean pbm$autoWeight;

    @Unique
    private boolean pbm$autoStunArmor;

    @Inject(method = "byItem", at = @At("HEAD"), remap = false, require = 0)
    private void pbm$captureAutoCalculatedFlags(Item item, CallbackInfoReturnable<Object> cir) {
        this.pbm$autoWeight = this.weight < 0.0D;
        this.pbm$autoStunArmor = this.stunArmor < 0.0D;
    }

    @Inject(method = "byItem", at = @At("RETURN"), remap = false, require = 0)
    private void pbm$halveEpicFightAutoArmorAttributes(Item item, CallbackInfoReturnable<Object> cir) {
        if (!(item instanceof ArmorItem)) {
            return;
        }

        double armorScaleFactor = ArmorCalculationHelper.getArmorScaleFactor();
        if (armorScaleFactor <= 0.0D) {
            return;
        }

        if (this.pbm$autoWeight && this.weight >= 0.0D) {
            this.weight /= armorScaleFactor;
        }

        if (this.pbm$autoStunArmor && this.stunArmor >= 0.0D) {
            this.stunArmor /= armorScaleFactor;
        }
    }
}