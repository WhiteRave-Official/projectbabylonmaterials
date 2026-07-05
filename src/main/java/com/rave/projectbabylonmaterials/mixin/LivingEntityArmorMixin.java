package com.rave.projectbabylonmaterials.mixin;

import com.rave.projectbabylonmaterials.combat.ArmorCalculationHelper;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityArmorMixin {
    @Shadow
    protected abstract void hurtArmor(DamageSource damageSource, float damage);

    @Shadow
    public abstract int getArmorValue();

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void projectBabylonMaterials(DamageSource damageSource, float damage, CallbackInfoReturnable<Float> cir) {
        if (damageSource.is(DamageTypeTags.BYPASSES_ARMOR)) {
            return;
        }

        if (Float.isNaN(damage) || Float.isInfinite(damage)) {
            cir.setReturnValue(0.0F);
            return;
        }

        if (ArmorCalculationHelper.shouldBypassPhysicalArmor(damageSource)) {
            cir.setReturnValue(Math.max(0.0F, damage));
            return;
        }

        this.hurtArmor(damageSource, damage);

        float armorValue = this.getArmorValue();
        if (armorValue <= 0.0F) {
            return;
        }

        LivingEntity self = (LivingEntity) (Object) this;
        float adjustedDamage = ArmorCalculationHelper.applyAdjustedArmorFormula(self, damageSource, damage, armorValue);
        cir.setReturnValue(Math.max(0.0F, adjustedDamage));
    }
}