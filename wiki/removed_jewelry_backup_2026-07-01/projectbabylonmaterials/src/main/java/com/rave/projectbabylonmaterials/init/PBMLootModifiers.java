package com.rave.projectbabylonmaterials.init;

import com.mojang.serialization.Codec;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.loot.ChestRarityLootModifier;
import com.rave.projectbabylonmaterials.loot.GemLootModifier;
import com.rave.projectbabylonmaterials.loot.MaterialLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class PBMLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ProjectBabylonMaterials.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> CHEST_RARITY =
            LOOT_MODIFIER_SERIALIZERS.register("chest_rarity", () -> ChestRarityLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> GEM_LOOT =
            LOOT_MODIFIER_SERIALIZERS.register("gem_loot", () -> GemLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> MATERIAL_LOOT =
            LOOT_MODIFIER_SERIALIZERS.register("material_loot", () -> MaterialLootModifier.CODEC);

    private PBMLootModifiers() {
    }

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
    }
}