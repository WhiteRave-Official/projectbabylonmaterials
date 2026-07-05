package com.rave.projectbabylonmaterials.init;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * In 1.21 enchantments are data-driven. The custom Magic Resistance enchantment is defined as a
 * datapack JSON under {@code data/project_babylon_materials/enchantment/magic_resistance.json};
 * code references it through this {@link ResourceKey} and resolves a {@code Holder<Enchantment>}
 * from the registry access at runtime.
 */
public final class PBMEnchantments {
    public static final ResourceKey<Enchantment> MAGIC_RESISTANCE =
            ResourceKey.create(Registries.ENCHANTMENT,
                    ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "magic_resistance"));

    private PBMEnchantments() {
    }
}
