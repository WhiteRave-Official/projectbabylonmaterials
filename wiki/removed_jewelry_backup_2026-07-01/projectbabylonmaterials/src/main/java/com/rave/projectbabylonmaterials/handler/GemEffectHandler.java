package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.gem.GemSlotHelper;
import com.rave.projectbabylonmaterials.gem.GemType;
import com.rave.projectbabylonmaterials.init.PBAttributes;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;
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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GemEffectHandler {
    private static final String ARROW_GEM_PROCESSED_TAG = "project_babylon_materials.arrow_gems_processed";
    private static final String ARROW_DAMAGE_MULTIPLIER_TAG = "project_babylon_materials.arrow_damage_multiplier";
    private static final String ARROW_VELOCITY_MULTIPLIER_TAG = "project_babylon_materials.arrow_velocity_multiplier";

    private static final UUID RUBY_ATTACK_DAMAGE_ID = UUID.fromString("0d7d7771-311e-4d79-aeea-4a0ddcc5e0f1");
    private static final UUID TOPAZ_ATTACK_SPEED_ID = UUID.fromString("4963b3dd-fac6-4782-95cf-a2f9e176fdbf");
    private static final UUID WHITE_CRIT_CHANCE_ID = UUID.fromString("7e6f7c68-5042-4514-a20f-0872c72ff65d");
    private static final UUID BLACK_CRIT_DAMAGE_ID = UUID.fromString("ee4d4f31-f6e3-49f3-abbb-a3c3f3a3cc31");
    private static final UUID HEALTH_MAX_HEALTH_ID = UUID.fromString("c5b2c99f-7e06-48de-9e58-3306ddf153f2");
    private static final UUID AQUAMARINE_MOVE_SPEED_ID = UUID.fromString("654283ca-12eb-45ec-b775-4544866b95d5");
    private static final UUID GARNET_DRAW_SPEED_ID = UUID.fromString("579f60ad-d5f6-48a6-90f8-5a4b81a79d45");
    private static final UUID SAPPHIRE_ARMOR_NEGATION_ID = UUID.fromString("f7d9cb0a-9170-4c16-81ef-c7fd8a42c65b");
    private static final UUID CHRIZOLITE_IMPACT_ID = UUID.fromString("feec4c13-5d57-40fe-bd76-ae3f54f7f1b7");
    private static final UUID EMERALD_STAMINA_ID = UUID.fromString("ef1e923f-b89a-4c05-a488-7d7bd67d19dc");
    private static final UUID MANA_MAX_MANA_ID = UUID.fromString("72d89096-2b64-4749-8d98-6dceab5fe457");
    private static final UUID DIAMOND_CAST_TIME_ID = UUID.fromString("87fc2e45-e45e-42ff-a0eb-b8f9fbc8c913");
    private static final UUID AMETHYST_COOLDOWN_ID = UUID.fromString("28e2b0d0-1a95-48c6-b363-f6f14252f430");
    private static final UUID END_SPELL_POWER_ID = UUID.fromString("60166508-d10a-4d23-822d-3781d0d5932b");
    private static final UUID BLOOD_SPELL_POWER_ID = UUID.fromString("9e5d2db7-b54e-4e2a-b1cb-5856281f652e");
    private static final UUID NORTHERN_SPELL_POWER_ID = UUID.fromString("c6b7801f-0909-4d9c-bb2c-08681741434f");
    private static final UUID PYRITE_SPELL_POWER_ID = UUID.fromString("8401efdb-a387-4af8-9b9a-770d62f242ff");
    private static final UUID MOON_PEARL_SPELL_POWER_ID = UUID.fromString("7b3443e4-4069-4523-879d-3544fdb55934");
    private static final UUID DRAGON_SPELL_POWER_ID = UUID.fromString("1144c347-e0ad-4fbe-af5b-79f811f5d3cb");
    private static final UUID NATURE_SPELL_POWER_ID = UUID.fromString("b660103f-2d52-4cb0-8afc-27626060b6a7");

    private static final ResourceLocation EPICFIGHT_ARMOR_NEGATION = new ResourceLocation("epicfight", "armor_negation");
    private static final ResourceLocation EPICFIGHT_IMPACT = new ResourceLocation("epicfight", "impact");
    private static final ResourceLocation EPICFIGHT_STAMINA = new ResourceLocation("epicfight", "staminar");
    private static final ResourceLocation ISB_MAX_MANA = new ResourceLocation("irons_spellbooks", "max_mana");
    private static final ResourceLocation ISB_CAST_TIME_REDUCTION = new ResourceLocation("irons_spellbooks", "cast_time_reduction");
    private static final ResourceLocation ISB_COOLDOWN_REDUCTION = new ResourceLocation("irons_spellbooks", "cooldown_reduction");
    private static final ResourceLocation ISB_ENDER_SPELL_POWER = new ResourceLocation("irons_spellbooks", "ender_spell_power");
    private static final ResourceLocation ISB_BLOOD_SPELL_POWER = new ResourceLocation("irons_spellbooks", "blood_spell_power");
    private static final ResourceLocation ISB_ICE_SPELL_POWER = new ResourceLocation("irons_spellbooks", "ice_spell_power");
    private static final ResourceLocation ISB_FIRE_SPELL_POWER = new ResourceLocation("irons_spellbooks", "fire_spell_power");
    private static final ResourceLocation ISB_HOLY_SPELL_POWER = new ResourceLocation("irons_spellbooks", "holy_spell_power");
    private static final ResourceLocation ISB_LIGHTNING_SPELL_POWER = new ResourceLocation("irons_spellbooks", "lightning_spell_power");
    private static final ResourceLocation ISB_NATURE_SPELL_POWER = new ResourceLocation("irons_spellbooks", "nature_spell_power");

    private GemEffectHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        if (event.player.tickCount % 10 != 0) {
            return;
        }

        applySocketedGemBonuses(event.player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0.0F || event.getEntity().level().isClientSide()) {
            return;
        }

        if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) {
            return;
        }

        double damageMultiplier = arrow.getPersistentData().getDouble(ARROW_DAMAGE_MULTIPLIER_TAG);
        if (damageMultiplier > 0.0D) {
            event.setAmount((float) (event.getAmount() * (1.0D + damageMultiplier)));
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

        applyModifier(player, Attributes.ATTACK_DAMAGE, RUBY_ATTACK_DAMAGE_ID, "project_babylon_materials.gem_ruby", bonuses.attackDamageMultiplier, AttributeModifier.Operation.MULTIPLY_TOTAL);
        applyModifier(player, Attributes.ATTACK_SPEED, TOPAZ_ATTACK_SPEED_ID, "project_babylon_materials.gem_topaz", bonuses.attackSpeedMultiplier, AttributeModifier.Operation.MULTIPLY_TOTAL);
        applyModifier(player, PBAttributes.CRIT_CHANCE.get(), WHITE_CRIT_CHANCE_ID, "project_babylon_materials.gem_white", bonuses.critChanceBonus, AttributeModifier.Operation.ADDITION);
        applyModifier(player, PBAttributes.CRIT_DAMAGE.get(), BLACK_CRIT_DAMAGE_ID, "project_babylon_materials.gem_black", bonuses.critDamageBonus, AttributeModifier.Operation.ADDITION);
        applyModifier(player, Attributes.MAX_HEALTH, HEALTH_MAX_HEALTH_ID, "project_babylon_materials.gem_health", bonuses.maxHealthMultiplier, AttributeModifier.Operation.MULTIPLY_TOTAL);
        applyModifier(player, Attributes.MOVEMENT_SPEED, AQUAMARINE_MOVE_SPEED_ID, "project_babylon_materials.gem_aquamarine", bonuses.movementSpeedMultiplier, AttributeModifier.Operation.MULTIPLY_TOTAL);
        applyModifier(player, PBAttributes.RANGED_DRAW_SPEED.get(), GARNET_DRAW_SPEED_ID, "project_babylon_materials.gem_garnet", bonuses.rangedDrawSpeedBonus, AttributeModifier.Operation.ADDITION);

        applyExternalModifier(player, EPICFIGHT_ARMOR_NEGATION, SAPPHIRE_ARMOR_NEGATION_ID, "project_babylon_materials.gem_sapphire", bonuses.armorNegationBonus, AttributeModifier.Operation.ADDITION);
        applyExternalModifier(player, EPICFIGHT_IMPACT, CHRIZOLITE_IMPACT_ID, "project_babylon_materials.gem_chrizolite", bonuses.impactBonus, AttributeModifier.Operation.ADDITION);
        applyExternalModifier(player, EPICFIGHT_STAMINA, EMERALD_STAMINA_ID, "project_babylon_materials.gem_emerald", bonuses.staminaMultiplier, AttributeModifier.Operation.MULTIPLY_TOTAL);

        applyExternalModifier(player, ISB_MAX_MANA, MANA_MAX_MANA_ID, "project_babylon_materials.gem_mana", bonuses.maxManaMultiplier, AttributeModifier.Operation.MULTIPLY_TOTAL);
        applyExternalModifier(player, ISB_CAST_TIME_REDUCTION, DIAMOND_CAST_TIME_ID, "project_babylon_materials.gem_diamond", bonuses.castTimeReductionBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        applyExternalModifier(player, ISB_COOLDOWN_REDUCTION, AMETHYST_COOLDOWN_ID, "project_babylon_materials.gem_amethyst", bonuses.cooldownReductionBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        applyExternalModifier(player, ISB_ENDER_SPELL_POWER, END_SPELL_POWER_ID, "project_babylon_materials.gem_end", bonuses.enderSpellPowerBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        applyExternalModifier(player, ISB_BLOOD_SPELL_POWER, BLOOD_SPELL_POWER_ID, "project_babylon_materials.gem_blood", bonuses.bloodSpellPowerBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        applyExternalModifier(player, ISB_ICE_SPELL_POWER, NORTHERN_SPELL_POWER_ID, "project_babylon_materials.gem_northern", bonuses.iceSpellPowerBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        applyExternalModifier(player, ISB_FIRE_SPELL_POWER, PYRITE_SPELL_POWER_ID, "project_babylon_materials.gem_pyrite", bonuses.fireSpellPowerBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        applyExternalModifier(player, ISB_HOLY_SPELL_POWER, MOON_PEARL_SPELL_POWER_ID, "project_babylon_materials.gem_moon_pearl", bonuses.holySpellPowerBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        applyExternalModifier(player, ISB_LIGHTNING_SPELL_POWER, DRAGON_SPELL_POWER_ID, "project_babylon_materials.gem_dragon", bonuses.lightningSpellPowerBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        applyExternalModifier(player, ISB_NATURE_SPELL_POWER, NATURE_SPELL_POWER_ID, "project_babylon_materials.gem_nature", bonuses.natureSpellPowerBonus, AttributeModifier.Operation.MULTIPLY_BASE);

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

    private static void applyModifier(LivingEntity entity, Attribute attribute, UUID modifierId, String modifierName, double amount, AttributeModifier.Operation operation) {
        AttributeInstance attributeInstance = entity.getAttribute(attribute);
        if (attributeInstance == null) {
            return;
        }

        AttributeModifier existing = attributeInstance.getModifier(modifierId);
        if (existing != null) {
            attributeInstance.removeModifier(modifierId);
        }

        if (Math.abs(amount) > 0.0001D) {
            attributeInstance.addTransientModifier(new AttributeModifier(modifierId, modifierName, amount, operation));
        }
    }

    private static void applyExternalModifier(LivingEntity entity, ResourceLocation attributeId, UUID modifierId, String modifierName, double amount, AttributeModifier.Operation operation) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
        if (attribute == null) {
            return;
        }

        applyModifier(entity, attribute, modifierId, modifierName, amount, operation);
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
