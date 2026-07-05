package com.rave.projectbabylonmaterials.init;

import com.mojang.serialization.MapCodec;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.loot.ChestRarityLootModifier;
import com.rave.projectbabylonmaterials.loot.GemLootModifier;
import com.rave.projectbabylonmaterials.loot.MaterialLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class PBMLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ProjectBabylonMaterials.MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<ChestRarityLootModifier>> CHEST_RARITY =
            LOOT_MODIFIER_SERIALIZERS.register("chest_rarity", () -> ChestRarityLootModifier.CODEC);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<GemLootModifier>> GEM_LOOT =
            LOOT_MODIFIER_SERIALIZERS.register("gem_loot", () -> GemLootModifier.CODEC);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<MaterialLootModifier>> MATERIAL_LOOT =
            LOOT_MODIFIER_SERIALIZERS.register("material_loot", () -> MaterialLootModifier.CODEC);

    private PBMLootModifiers() {
    }

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
    }
}
