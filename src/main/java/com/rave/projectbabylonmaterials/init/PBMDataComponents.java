package com.rave.projectbabylonmaterials.init;

import com.mojang.serialization.Codec;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

/**
 * Custom {@link DataComponentType}s that replace the item NBT used in the 1.20.1 build
 * (PBRarity / PBGemSlotCount / PBGems / PBGemUpgradeAttempts / PBEnchantSlotCount).
 */
public final class PBMDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(ProjectBabylonMaterials.MODID);

    public static final Supplier<DataComponentType<ItemRarityTier>> RARITY =
            DATA_COMPONENTS.registerComponentType("rarity", builder -> builder
                    .persistent(Codec.STRING.xmap(ItemRarityTier::byId, ItemRarityTier::getId))
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8.map(ItemRarityTier::byId, ItemRarityTier::getId)));

    public static final Supplier<DataComponentType<Integer>> GEM_SLOT_COUNT =
            DATA_COMPONENTS.registerComponentType("gem_slot_count", builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final Supplier<DataComponentType<List<ItemStack>>> SOCKETED_GEMS =
            DATA_COMPONENTS.registerComponentType("socketed_gems", builder -> builder
                    .persistent(ItemStack.CODEC.listOf())
                    .networkSynchronized(ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list())));

    public static final Supplier<DataComponentType<Integer>> GEM_UPGRADE_ATTEMPTS =
            DATA_COMPONENTS.registerComponentType("gem_upgrade_attempts", builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final Supplier<DataComponentType<Integer>> ENCHANT_SLOT_COUNT =
            DATA_COMPONENTS.registerComponentType("enchant_slot_count", builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    private PBMDataComponents() {
    }

    public static void register(IEventBus modBus) {
        DATA_COMPONENTS.register(modBus);
    }
}
