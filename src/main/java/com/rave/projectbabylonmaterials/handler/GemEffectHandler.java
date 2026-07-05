package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.gem.GemSlotHelper;
import com.rave.projectbabylonmaterials.gem.GemType;
import com.rave.projectbabylonmaterials.init.PBAttributes;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Optional;

public final class GemEffectHandler {
    private static final String ARROW_GEM_PROCESSED_TAG = "project_babylon_materials.arrow_gems_processed";
    private static final String ARROW_DAMAGE_MULTIPLIER_TAG = "project_babylon_materials.arrow_damage_multiplier";
    private static final String ARROW_VELOCITY_MULTIPLIER_TAG = "project_babylon_materials.arrow_velocity_multiplier";

    private static final ResourceLocation RUBY_ATTACK_DAMAGE_ID = id("gem_ruby");
    private static final ResourceLocation TOPAZ_ATTACK_SPEED_ID = id("gem_topaz");
    private static final ResourceLocation WHITE_CRIT_CHANCE_ID = id("gem_white");
    private static final ResourceLocation BLACK_CRIT_DAMAGE_ID = id("gem_black");
    private static final ResourceLocation HEALTH_MAX_HEALTH_ID = id("gem_health");
    private static final ResourceLocation AQUAMARINE_MOVE_SPEED_ID = id("gem_aquamarine");
    private static final ResourceLocation GARNET_DRAW_SPEED_ID = id("gem_garnet");
    private static final ResourceLocation SAPPHIRE_ARMOR_NEGATION_ID = id("gem_sapphire");
    private static final ResourceLocation CHRIZOLITE_IMPACT_ID = id("gem_chrizolite");
    private static final ResourceLocation EMERALD_STAMINA_ID = id("gem_emerald");
    private static final ResourceLocation MANA_MAX_MANA_ID = id("gem_mana");
    private static final ResourceLocation DIAMOND_CAST_TIME_ID = id("gem_diamond");
    private static final ResourceLocation AMETHYST_COOLDOWN_ID = id("gem_amethyst");
    private static final ResourceLocation END_SPELL_POWER_ID = id("gem_end");
    private static final ResourceLocation BLOOD_SPELL_POWER_ID = id("gem_blood");
    private static final ResourceLocation NORTHERN_SPELL_POWER_ID = id("gem_northern");
    private static final ResourceLocation PYRITE_SPELL_POWER_ID = id("gem_pyrite");
    private static final ResourceLocation MOON_PEARL_SPELL_POWER_ID = id("gem_moon_pearl");
    private static final ResourceLocation DRAGON_SPELL_POWER_ID = id("gem_dragon");
    private static final ResourceLocation NATURE_SPELL_POWER_ID = id("gem_nature");

    // TODO(port): third-party attribute IDs (epicfight / irons_spellbooks) for 1.21.1 builds.
    // Resolved leniently via the registry; absent attributes are simply skipped (no-op).
    private static final ResourceLocation EPICFIGHT_ARMOR_NEGATION = ResourceLocation.fromNamespaceAndPath("epicfight", "armor_negation");
    private static final ResourceLocation EPICFIGHT_IMPACT = ResourceLocation.fromNamespaceAndPath("epicfight", "impact");
    private static final ResourceLocation EPICFIGHT_STAMINA = ResourceLocation.fromNamespaceAndPath("epicfight", "staminar");
    private static final ResourceLocation ISB_MAX_MANA = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "max_mana");
    private static final ResourceLocation ISB_CAST_TIME_REDUCTION = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "cast_time_reduction");
    private static final ResourceLocation ISB_COOLDOWN_REDUCTION = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "cooldown_reduction");
    private static final ResourceLocation ISB_ENDER_SPELL_POWER = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ender_spell_power");
    private static final ResourceLocation ISB_BLOOD_SPELL_POWER = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "blood_spell_power");
    private static final ResourceLocation ISB_ICE_SPELL_POWER = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ice_spell_power");
    private static final ResourceLocation ISB_FIRE_SPELL_POWER = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "fire_spell_power");
    private static final ResourceLocation ISB_HOLY_SPELL_POWER = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "holy_spell_power");
    private static final ResourceLocation ISB_LIGHTNING_SPELL_POWER = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "lightning_spell_power");
    private static final ResourceLocation ISB_NATURE_SPELL_POWER = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "nature_spell_power");

    private GemEffectHandler() {
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, path);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        if (player.tickCount % 10 != 0) {
            return;
        }

        applySocketedGemBonuses(player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getNewDamage() <= 0.0F || event.getEntity().level().isClientSide()) {
            return;
        }

        if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) {
            return;
        }

        double damageMultiplier = arrow.getPersistentData().getDouble(ARROW_DAMAGE_MULTIPLIER_TAG);
        if (damageMultiplier > 0.0D) {
            event.setNewDamage((float) (event.getNewDamage() * (1.0D + damageMultiplier)));
        }
    }

    @SubscribeEvent
    public static void onArrowSpawn(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof AbstractArrow arrow)) {
            return;
        }

        if (arrow.getPersistentData().getBoolean(ARROW_GEM_PROCESSED_TAG)) {
            return;
        }

        if (!(arrow.getOwner() instanceof LivingEntity owner)) {
            return;
        }

        ItemStack rangedWeapon = findRangedWeaponInHands(owner);
        if (rangedWeapon.isEmpty()) {
            arrow.getPersistentData().putBoolean(ARROW_GEM_PROCESSED_TAG, true);
            return;
        }

        GemBonuses bonuses = new GemBonuses();
        collectBonuses(rangedWeapon, bonuses);

        if (bonuses.arrowDamageMultiplier > 0.0D) {
            arrow.getPersistentData().putDouble(ARROW_DAMAGE_MULTIPLIER_TAG, bonuses.arrowDamageMultiplier);
        }

        if (bonuses.arrowVelocityMultiplier > 0.0D) {
            arrow.getPersistentData().putDouble(ARROW_VELOCITY_MULTIPLIER_TAG, bonuses.arrowVelocityMultiplier);
            arrow.setDeltaMovement(arrow.getDeltaMovement().scale(1.0D + bonuses.arrowVelocityMultiplier));
        }

        arrow.getPersistentData().putBoolean(ARROW_GEM_PROCESSED_TAG, true);
    }

    public static int adjustBowCharge(ItemStack bowStack, int originalCharge) {
        double drawSpeedBonus = getRangedDrawSpeedBonus(bowStack);
        if (drawSpeedBonus <= 0.0D) {
            return originalCharge;
        }

        return Math.max(0, Mth.ceil(originalCharge * (1.0D + drawSpeedBonus)));
    }

    public static int adjustCrossbowChargeDuration(ItemStack crossbowStack, int originalDuration) {
        double drawSpeedBonus = getRangedDrawSpeedBonus(crossbowStack);
        if (drawSpeedBonus <= 0.0D) {
            return originalDuration;
        }

        return Math.max(1, Mth.floor(originalDuration / (1.0D + drawSpeedBonus)));
    }

    private static double getRangedDrawSpeedBonus(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0.0D;
        }

        GemBonuses bonuses = new GemBonuses();
        collectBonuses(stack, bonuses);
        return bonuses.rangedDrawSpeedBonus;
    }

    private static void applySocketedGemBonuses(Player player) {
        GemBonuses bonuses = new GemBonuses();
        collectBonuses(player.getMainHandItem(), bonuses);
        collectBonuses(player.getOffhandItem(), bonuses);
        for (ItemStack armorPiece : player.getArmorSlots()) {
            collectBonuses(armorPiece, bonuses);
        }

        double previousMaxHealth = player.getMaxHealth();
        float previousHealth = player.getHealth();

        applyModifier(player, Attributes.ATTACK_DAMAGE, RUBY_ATTACK_DAMAGE_ID, bonuses.attackDamageMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        applyModifier(player, Attributes.ATTACK_SPEED, TOPAZ_ATTACK_SPEED_ID, bonuses.attackSpeedMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        applyModifier(player, PBAttributes.CRIT_CHANCE, WHITE_CRIT_CHANCE_ID, bonuses.critChanceBonus, AttributeModifier.Operation.ADD_VALUE);
        applyModifier(player, PBAttributes.CRIT_DAMAGE, BLACK_CRIT_DAMAGE_ID, bonuses.critDamageBonus, AttributeModifier.Operation.ADD_VALUE);
        applyModifier(player, Attributes.MAX_HEALTH, HEALTH_MAX_HEALTH_ID, bonuses.maxHealthMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        applyModifier(player, Attributes.MOVEMENT_SPEED, AQUAMARINE_MOVE_SPEED_ID, bonuses.movementSpeedMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        applyModifier(player, PBAttributes.RANGED_DRAW_SPEED, GARNET_DRAW_SPEED_ID, bonuses.rangedDrawSpeedBonus, AttributeModifier.Operation.ADD_VALUE);

        applyExternalModifier(player, EPICFIGHT_ARMOR_NEGATION, SAPPHIRE_ARMOR_NEGATION_ID, bonuses.armorNegationBonus, AttributeModifier.Operation.ADD_VALUE);
        applyExternalModifier(player, EPICFIGHT_IMPACT, CHRIZOLITE_IMPACT_ID, bonuses.impactBonus, AttributeModifier.Operation.ADD_VALUE);
        applyExternalModifier(player, EPICFIGHT_STAMINA, EMERALD_STAMINA_ID, bonuses.staminaMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        applyExternalModifier(player, ISB_MAX_MANA, MANA_MAX_MANA_ID, bonuses.maxManaMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        applyExternalModifier(player, ISB_CAST_TIME_REDUCTION, DIAMOND_CAST_TIME_ID, bonuses.castTimeReductionBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        applyExternalModifier(player, ISB_COOLDOWN_REDUCTION, AMETHYST_COOLDOWN_ID, bonuses.cooldownReductionBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        applyExternalModifier(player, ISB_ENDER_SPELL_POWER, END_SPELL_POWER_ID, bonuses.enderSpellPowerBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        applyExternalModifier(player, ISB_BLOOD_SPELL_POWER, BLOOD_SPELL_POWER_ID, bonuses.bloodSpellPowerBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        applyExternalModifier(player, ISB_ICE_SPELL_POWER, NORTHERN_SPELL_POWER_ID, bonuses.iceSpellPowerBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        applyExternalModifier(player, ISB_FIRE_SPELL_POWER, PYRITE_SPELL_POWER_ID, bonuses.fireSpellPowerBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        applyExternalModifier(player, ISB_HOLY_SPELL_POWER, MOON_PEARL_SPELL_POWER_ID, bonuses.holySpellPowerBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        applyExternalModifier(player, ISB_LIGHTNING_SPELL_POWER, DRAGON_SPELL_POWER_ID, bonuses.lightningSpellPowerBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        applyExternalModifier(player, ISB_NATURE_SPELL_POWER, NATURE_SPELL_POWER_ID, bonuses.natureSpellPowerBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

        double newMaxHealth = player.getMaxHealth();
        if (player instanceof ServerPlayer && newMaxHealth < previousHealth) {
            player.setHealth((float) Mth.clamp(previousHealth, 0.0D, newMaxHealth));
        } else if (player instanceof ServerPlayer && Math.abs(previousMaxHealth - newMaxHealth) > 0.0001D && previousMaxHealth > 0.0D && previousHealth > previousMaxHealth) {
            player.setHealth((float) Mth.clamp(previousHealth, 0.0D, newMaxHealth));
        }
    }

    private static void collectBonuses(ItemStack stack, GemBonuses bonuses) {
        if (stack.isEmpty()) {
            return;
        }

        List<ItemStack> socketedGems = GemSlotHelper.getSocketedGems(stack);
        for (ItemStack gemStack : socketedGems) {
            Optional<GemType> gemTypeOptional = GemType.fromStack(gemStack);
            Optional<ItemRarityTier> rarityOptional = ItemRarityHelper.getRarity(gemStack);
            if (gemTypeOptional.isEmpty() || rarityOptional.isEmpty()) {
                continue;
            }

            GemType gemType = gemTypeOptional.get();
            double percentValue = gemType.getValue(rarityOptional.get());
            double decimalValue = percentValue / 100.0D;

            switch (gemType) {
                case RUBY -> bonuses.attackDamageMultiplier += decimalValue;
                case SAPPHIRE -> bonuses.armorNegationBonus += percentValue;
                case TOPAZ -> bonuses.attackSpeedMultiplier += decimalValue;
                case WHITE -> bonuses.critChanceBonus += decimalValue;
                case BLACK -> bonuses.critDamageBonus += decimalValue;
                case CHRIZOLITE -> bonuses.impactBonus += percentValue;
                case MALACHITE -> bonuses.arrowDamageMultiplier += decimalValue;
                case GARNET -> bonuses.rangedDrawSpeedBonus += decimalValue;
                case LAPIS -> bonuses.arrowVelocityMultiplier += decimalValue;
                case MANA -> bonuses.maxManaMultiplier += decimalValue;
                case END -> bonuses.enderSpellPowerBonus += decimalValue;
                case BLOOD_PEARL -> bonuses.bloodSpellPowerBonus += decimalValue;
                case NORTHERN -> bonuses.iceSpellPowerBonus += decimalValue;
                case PYRITE -> bonuses.fireSpellPowerBonus += decimalValue;
                case MOON_PEARL -> bonuses.holySpellPowerBonus += decimalValue;
                case DRAGON -> bonuses.lightningSpellPowerBonus += decimalValue;
                case NATURE -> bonuses.natureSpellPowerBonus += decimalValue;
                case DIAMOND -> bonuses.castTimeReductionBonus += decimalValue;
                case AMETHYST -> bonuses.cooldownReductionBonus += decimalValue;
                case HEALTH -> bonuses.maxHealthMultiplier += decimalValue;
                case EMERALD -> bonuses.staminaMultiplier += decimalValue;
                case AQUAMARINE -> bonuses.movementSpeedMultiplier += decimalValue;
            }
        }
    }

    private static void applyModifier(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation modifierId, double amount, AttributeModifier.Operation operation) {
        AttributeInstance attributeInstance = entity.getAttribute(attribute);
        if (attributeInstance == null) {
            return;
        }

        if (attributeInstance.getModifier(modifierId) != null) {
            attributeInstance.removeModifier(modifierId);
        }

        if (Math.abs(amount) > 0.0001D) {
            attributeInstance.addTransientModifier(new AttributeModifier(modifierId, amount, operation));
        }
    }

    private static void applyExternalModifier(LivingEntity entity, ResourceLocation attributeId, ResourceLocation modifierId, double amount, AttributeModifier.Operation operation) {
        Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeId);
        if (attribute == null) {
            return;
        }

        applyModifier(entity, BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute), modifierId, amount, operation);
    }

    private static ItemStack findRangedWeaponInHands(LivingEntity owner) {
        ItemStack mainHand = owner.getMainHandItem();
        if (mainHand.getItem() instanceof BowItem || mainHand.getItem() instanceof CrossbowItem) {
            return mainHand;
        }

        ItemStack offHand = owner.getOffhandItem();
        if (offHand.getItem() instanceof BowItem || offHand.getItem() instanceof CrossbowItem) {
            return offHand;
        }

        return ItemStack.EMPTY;
    }

    private static final class GemBonuses {
        private double attackDamageMultiplier;
        private double attackSpeedMultiplier;
        private double critChanceBonus;
        private double critDamageBonus;
        private double maxHealthMultiplier;
        private double movementSpeedMultiplier;
        private double rangedDrawSpeedBonus;
        private double armorNegationBonus;
        private double impactBonus;
        private double staminaMultiplier;
        private double arrowDamageMultiplier;
        private double arrowVelocityMultiplier;
        private double maxManaMultiplier;
        private double castTimeReductionBonus;
        private double cooldownReductionBonus;
        private double enderSpellPowerBonus;
        private double bloodSpellPowerBonus;
        private double iceSpellPowerBonus;
        private double fireSpellPowerBonus;
        private double holySpellPowerBonus;
        private double lightningSpellPowerBonus;
        private double natureSpellPowerBonus;
    }
}
