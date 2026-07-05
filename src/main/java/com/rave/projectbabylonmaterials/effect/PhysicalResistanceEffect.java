package com.rave.projectbabylonmaterials.effect;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.combat.EnchantmentRebalanceHelper;
import com.rave.projectbabylonmaterials.init.PBMEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ProjectBabylonMaterials.MODID)
public class PhysicalResistanceEffect extends MobEffect {
    public static final float DAMAGE_REDUCTION = 0.10F;

    public PhysicalResistanceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x7AA6B8);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0.0F) {
            return;
        }

        if (!event.getEntity().hasEffect(PBMEffects.PHYSICAL_RESISTANCE)) {
            return;
        }

        if (!EnchantmentRebalanceHelper.isPhysicalDamage(event.getSource())) {
            return;
        }

        event.setAmount(event.getAmount() * (1.0F - DAMAGE_REDUCTION));
    }
}
