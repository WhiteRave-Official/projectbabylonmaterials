package com.rave.projectbabylonmaterials.mixin;

import com.rave.projectbabylonmaterials.combat.MagicArmorCalculationHelper;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "io.redspace.ironsspellbooks.damage.DamageSources", remap = false)
public class IronsSpellbooksDamageSourcesMixin {
    @Inject(method = "getResist", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void pbm(LivingEntity target, SchoolType schoolType, CallbackInfoReturnable<Float> cir) {
        // TODO(port): verify against Iron's Spellbooks 1.21.1 (3.16.x) — AttributeRegistry.SPELL_RESIST is a
        // NeoForge DeferredHolder<Attribute> (usable directly as Holder<Attribute>); SchoolType#getResistanceFor
        // and DamageSources#getResist(LivingEntity, SchoolType) signatures assumed unchanged.
        float spellResist = (float) target.getAttributeValue(AttributeRegistry.SPELL_RESIST);
        float schoolResist = schoolType == null ? 1.0F : (float) schoolType.getResistanceFor(target);
        cir.setReturnValue(MagicArmorCalculationHelper.applyAdjustedSpellResist(spellResist, schoolResist));
    }
}
