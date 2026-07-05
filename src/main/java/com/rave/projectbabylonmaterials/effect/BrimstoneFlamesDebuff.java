package com.rave.projectbabylonmaterials.effect;

import com.rave.projectbabylonmaterials.init.PBMEffects;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BrimstoneFlamesDebuff extends MobEffect {

    public static final float FIRE_DAMAGE_BONUS = 0.15F;
    private static final String FIRE_MAGIC_DAMAGE_TYPE = "irons_spellbooks:fire_magic";

    public BrimstoneFlamesDebuff() {
        super(MobEffectCategory.HARMFUL, 0xFF8A1A);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0.0F) {
            return;
        }

        if (!event.getEntity().hasEffect(PBMEffects.BRIMSTONE_FLAMES.get())) {
            return;
        }

        boolean isVanillaFire = event.getSource().is(DamageTypeTags.IS_FIRE);
        boolean isIssFireMagic = IronSpellbooksEffectCompat.isDamageSource(event.getSource(), FIRE_MAGIC_DAMAGE_TYPE);
        if (!isVanillaFire && !isIssFireMagic) {
            return;
        }

        event.setAmount(event.getAmount() * (1.0F + FIRE_DAMAGE_BONUS));
    }
}

