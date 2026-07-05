package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.combat.EnchantmentRebalanceHelper;
import com.rave.projectbabylonmaterials.mixin.AbstractArrowAccessor;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;

public final class VanillaEnchantmentRebalanceHandler {
    private static final String LAST_BOW_POWER_TAG = "project_babylon_materials.last_bow_power";
    private static final String LAST_BOW_FLAME_TAG = "project_babylon_materials.last_bow_flame";
    private static final String LAST_BOW_INFINITY_TAG = "project_babylon_materials.last_bow_infinity";
    private static final String LAST_BOW_TICK_TAG = "project_babylon_materials.last_bow_tick";
    private static final String ARROW_PROCESSED_TAG = "project_babylon_materials.arrow_enchants_processed";

    private VanillaEnchantmentRebalanceHandler() {
    }

    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        ItemStack bow = event.getBow();
        if (!(bow.getItem() instanceof BowItem) || event.getCharge() < 0) {
            return;
        }

        Player player = event.getEntity();
        CompoundTag data = player.getPersistentData();
        data.putInt(LAST_BOW_POWER_TAG, EnchantmentRebalanceHelper.levelOf(bow, Enchantments.POWER));
        data.putInt(LAST_BOW_FLAME_TAG, EnchantmentRebalanceHelper.levelOf(bow, Enchantments.FLAME));
        data.putInt(LAST_BOW_INFINITY_TAG, EnchantmentRebalanceHelper.levelOf(bow, Enchantments.INFINITY));
        data.putLong(LAST_BOW_TICK_TAG, event.getLevel().getGameTime());
        event.setCharge(GemEffectHandler.adjustBowCharge(bow, event.getCharge()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0.0F || event.getEntity().level().isClientSide) {
            return;
        }

        applyPhysicalProtection(event);
        applyMagicResistance(event);
        applyMeleeDamageEnchantments(event);
        applyFireAspect(event);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof AbstractArrow arrow)) {
            return;
        }

        CompoundTag data = arrow.getPersistentData();
        if (data.getBoolean(ARROW_PROCESSED_TAG)) {
            return;
        }

        if (!(arrow.getOwner() instanceof LivingEntity owner)) {
            return;
        }

        BowEnchantSnapshot snapshot = resolveBowEnchantSnapshot(owner);
        if (!snapshot.hasAnyRelevantEnchant()) {
            data.putBoolean(ARROW_PROCESSED_TAG, true);
            return;
        }

        applyPowerRebalance(arrow, snapshot.powerLevel());
        applyFlameRebalance(arrow, snapshot.flameLevel());
        applyInfinityRebalance(arrow, owner, snapshot.infinityLevel());
        data.putBoolean(ARROW_PROCESSED_TAG, true);
    }

    private static void applyPhysicalProtection(LivingIncomingDamageEvent event) {
        float reduction = EnchantmentRebalanceHelper.getPhysicalProtectionReduction(event.getEntity(), event.getSource());
        if (reduction <= 0.0F) {
            return;
        }

        event.setAmount(event.getAmount() * Math.max(0.0F, 1.0F - reduction));
    }

    private static void applyMagicResistance(LivingIncomingDamageEvent event) {
        float reduction = EnchantmentRebalanceHelper.getMagicResistanceReduction(event.getEntity(), event.getSource());
        if (reduction <= 0.0F) {
            return;
        }

        event.setAmount(event.getAmount() * Math.max(0.0F, 1.0F - reduction));
    }

    private static void applyMeleeDamageEnchantments(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        if (event.getSource().getDirectEntity() != attacker) {
            return;
        }

        ItemStack weapon = attacker.getMainHandItem();
        float multiplier = EnchantmentRebalanceHelper.getMeleeDamageMultiplier(weapon, event.getEntity());
        if (multiplier <= 0.0F) {
            return;
        }

        event.setAmount(event.getAmount() * (1.0F + multiplier));
    }

    private static void applyFireAspect(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        if (event.getSource().getDirectEntity() != attacker) {
            return;
        }

        ItemStack weapon = attacker.getMainHandItem();
        float chance = EnchantmentRebalanceHelper.getFireAspectChance(weapon);
        int durationSeconds = EnchantmentRebalanceHelper.getFireAspectDurationSeconds(weapon);
        if (chance <= 0.0F || durationSeconds <= 0) {
            return;
        }

        if (attacker.getRandom().nextFloat() < chance) {
            event.getEntity().igniteForSeconds(durationSeconds);
        }
    }

    private static void applyPowerRebalance(AbstractArrow arrow, int powerLevel) {
        if (powerLevel <= 0) {
            return;
        }

        double currentBaseDamage = arrow.getBaseDamage();
        double vanillaBonus = 0.5D * powerLevel + 0.5D;
        double originalBaseDamage = Math.max(0.0D, currentBaseDamage - vanillaBonus);
        double rebalancedDamage = originalBaseDamage * (1.0D + powerLevel * EnchantmentRebalanceHelper.POWER_PER_LEVEL);
        arrow.setBaseDamage(rebalancedDamage);
    }

    private static void applyFlameRebalance(AbstractArrow arrow, int flameLevel) {
        if (flameLevel <= 0) {
            return;
        }

        arrow.clearFire();
        if (arrow.level().random.nextFloat() < EnchantmentRebalanceHelper.FLAME_CHANCE) {
            arrow.igniteForSeconds(100);
        }
    }

    private static void applyInfinityRebalance(AbstractArrow arrow, LivingEntity owner, int infinityLevel) {
        if (infinityLevel <= 0 || !(owner instanceof Player player) || player.getAbilities().instabuild) {
            return;
        }

        ItemStack ammo = ((AbstractArrowAccessor) arrow).pbm$invokeGetPickupItem().copy();
        if (ammo.isEmpty()) {
            return;
        }
        ammo.setCount(1);

        boolean saveAmmo = player.getRandom().nextFloat() < EnchantmentRebalanceHelper.INFINITY_SAVE_CHANCE;
        if (saveAmmo) {
            if (!ammo.is(Items.ARROW)) {
                giveOrDropArrow(player, ammo);
            }
            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            return;
        }

        if (ammo.is(Items.ARROW)) {
            consumeMatchingArrow(player, ammo);
        }
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
    }

    private static void consumeMatchingArrow(Player player, ItemStack ammoTemplate) {
        if (shrinkMatchingStack(player.getInventory().offhand, ammoTemplate)) {
            return;
        }
        shrinkMatchingStack(player.getInventory().items, ammoTemplate);
    }

    private static boolean shrinkMatchingStack(NonNullList<ItemStack> stacks, ItemStack ammoTemplate) {
        for (ItemStack stack : stacks) {
            if (ItemStack.isSameItemSameComponents(stack, ammoTemplate) && !stack.isEmpty()) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    private static void giveOrDropArrow(Player player, ItemStack ammo) {
        if (!player.getInventory().add(ammo)) {
            player.drop(ammo, false);
        }
    }

    private static BowEnchantSnapshot resolveBowEnchantSnapshot(LivingEntity owner) {
        if (owner instanceof Player player) {
            CompoundTag data = player.getPersistentData();
            long currentTick = player.level().getGameTime();
            long snapshotTick = data.getLong(LAST_BOW_TICK_TAG);
            if (snapshotTick >= currentTick - 1L) {
                BowEnchantSnapshot snapshot = new BowEnchantSnapshot(
                        data.getInt(LAST_BOW_POWER_TAG),
                        data.getInt(LAST_BOW_FLAME_TAG),
                        data.getInt(LAST_BOW_INFINITY_TAG)
                );
                clearBowSnapshot(data);
                return snapshot;
            }
            clearBowSnapshot(data);
        }

        ItemStack bow = findBowInHands(owner);
        if (bow.isEmpty()) {
            return BowEnchantSnapshot.EMPTY;
        }

        return new BowEnchantSnapshot(
                EnchantmentRebalanceHelper.levelOf(bow, Enchantments.POWER),
                EnchantmentRebalanceHelper.levelOf(bow, Enchantments.FLAME),
                owner instanceof Player ? EnchantmentRebalanceHelper.levelOf(bow, Enchantments.INFINITY) : 0
        );
    }

    private static ItemStack findBowInHands(LivingEntity owner) {
        ItemStack mainHand = owner.getMainHandItem();
        if (mainHand.getItem() instanceof BowItem) {
            return mainHand;
        }

        ItemStack offHand = owner.getOffhandItem();
        if (offHand.getItem() instanceof BowItem) {
            return offHand;
        }

        return ItemStack.EMPTY;
    }

    private static void clearBowSnapshot(CompoundTag data) {
        data.remove(LAST_BOW_POWER_TAG);
        data.remove(LAST_BOW_FLAME_TAG);
        data.remove(LAST_BOW_INFINITY_TAG);
        data.remove(LAST_BOW_TICK_TAG);
    }

    private record BowEnchantSnapshot(int powerLevel, int flameLevel, int infinityLevel) {
        private static final BowEnchantSnapshot EMPTY = new BowEnchantSnapshot(0, 0, 0);

        private boolean hasAnyRelevantEnchant() {
            return powerLevel > 0 || flameLevel > 0 || infinityLevel > 0;
        }
    }
}
