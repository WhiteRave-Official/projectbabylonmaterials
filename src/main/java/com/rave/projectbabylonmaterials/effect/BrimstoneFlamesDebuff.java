package com.rave.projectbabylonmaterials.effect;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.init.PBMEffects;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ProjectBabylonMaterials.MODID)
public class BrimstoneFlamesDebuff extends MobEffect {

    public static final float FIRE_DAMAGE_BONUS = 0.15F;

    public BrimstoneFlamesDebuff() {
        super(MobEffectCategory.HARMFUL, 0xFF8A1A);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0.0F) {
            return;
        }

        if (!event.getEntity().hasEffect(PBMEffects.BRIMSTONE_FLAMES)) {
            return;
        }

        boolean isVanillaFire = event.getSource().is(DamageTypeTags.IS_FIRE);
        boolean isIssFireMagic = event.getSource().is(ISSDamageTypes.FIRE_MAGIC);
        if (!isVanillaFire && !isIssFireMagic) {
            return;
        }

        event.setAmount(event.getAmount() * (1.0F + FIRE_DAMAGE_BONUS));
    }
}
