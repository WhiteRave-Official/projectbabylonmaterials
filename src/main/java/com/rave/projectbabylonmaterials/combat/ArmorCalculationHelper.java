package com.rave.projectbabylonmaterials.combat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.lang.reflect.Method;
public final class ArmorCalculationHelper {
    private static final float ARMOR_SCALE_FACTOR = 2.0F;
    private static final float TOUGHNESS_SCALE_FACTOR = 2.0F;
    private static final float TOUGHNESS_NEGATION_FULL_AT = 40.0F;
    private static final float TOUGHNESS_NEGATION_CAP = 0.50F;
    private static final String IRONS_SPELLBOOKS_NAMESPACE = "irons_spellbooks";
    private ArmorCalculationHelper() {
    }
    public static float getArmorScaleFactor() {
        return ARMOR_SCALE_FACTOR;
    }
    public static float applyAdjustedArmorFormula(LivingEntity entity, DamageSource source, float damage, float armorValue) {
        float armorToughness = (float) entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        float effectiveArmorNegation = resolveEffectiveArmorNegation(source, armorToughness);
        float effectiveArmor = armorValue * (1.0F - effectiveArmorNegation / 100.0F);
        float scaledArmor = Math.max(0.0F, effectiveArmor) / ARMOR_SCALE_FACTOR;
        float scaledToughness = Math.max(0.0F, armorToughness) / TOUGHNESS_SCALE_FACTOR;
        return CombatRules.getDamageAfterAbsorb(entity, damage, source, scaledArmor, scaledToughness);
    }
    public static boolean shouldBypassPhysicalArmor(DamageSource source) {
        return isIronsSpellbooksDamage(source);
    }
    public static boolean isIronsSpellbooksDamage(DamageSource source) {
        return IRONS_SPELLBOOKS_NAMESPACE.equals(resolveDamageNamespace(source));
    }
    private static String resolveDamageNamespace(DamageSource source) {
        try {
            return source.typeHolder().unwrapKey()
                    .map(key -> key.location().getNamespace())
                    .orElse("");
        } catch (Exception ignored) {
        }
        try {
            Method typeMethod = source.getClass().getMethod("type");
            Object type = typeMethod.invoke(source);
            if (type != null) {
                Method msgIdMethod = type.getClass().getMethod("msgId");
                Object msgId = msgIdMethod.invoke(type);
                if (msgId instanceof String id && id.contains(":")) {
                    ResourceLocation location = ResourceLocation.tryParse(id);
                    return location != null ? location.getNamespace() : "";
                }
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return "";
    }
    private static float resolveEffectiveArmorNegation(DamageSource source, float armorToughness) {
        float baseArmorNegation = resolveArmorNegation(source);
        if (baseArmorNegation <= 0.0F) {
            return 0.0F;
        }
        float negationResistance = Math.min(Math.max(armorToughness / TOUGHNESS_NEGATION_FULL_AT, 0.0F), 1.0F) * TOUGHNESS_NEGATION_CAP;
        return Math.max(0.0F, baseArmorNegation * (1.0F - negationResistance));
    }
    private static float resolveArmorNegation(DamageSource source) {
        try {
            Method calculateArmorNegation = source.getClass().getMethod("calculateArmorNegation");
            Object result = calculateArmorNegation.invoke(source);
            if (result instanceof Number number) {
                return number.floatValue();
            }
        } catch (ReflectiveOperationException ignored) {
        }
        try {
            Method getBaseArmorNegation = source.getClass().getMethod("getBaseArmorNegation");
            Object result = getBaseArmorNegation.invoke(source);
            if (result instanceof Number number) {
                return number.floatValue();
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return 0.0F;
    }
}