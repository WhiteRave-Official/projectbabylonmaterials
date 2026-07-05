package com.rave.projectbabylonmaterials.balance;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

/**
 * Declares the two datapack registries that hold the mod's balance values and registers them (synced to
 * the client) during {@link DataPackRegistryEvent.NewRegistry}.
 *
 * <p>Datapack files live at:
 * <ul>
 *     <li>{@code data/<namespace>/rarity_balance/<rarity>.json} (common, uncommon, rare, epic, legendary)</li>
 *     <li>{@code data/<namespace>/gem_balance/<gem_registry_name>.json} (e.g. ruby_stone.json)</li>
 * </ul>
 */
public final class PBMBalanceRegistries {
    public static final ResourceKey<Registry<RarityBalance>> RARITY_BALANCE = ResourceKey.createRegistryKey(
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "rarity_balance"));
    public static final ResourceKey<Registry<GemBalance>> GEM_BALANCE = ResourceKey.createRegistryKey(
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "gem_balance"));

    private PBMBalanceRegistries() {
    }

    public static void registerDataPackRegistries(DataPackRegistryEvent.NewRegistry event) {
        // Passing a network codec marks the registry as synchronized, so values are available client-side
        // (tooltips, station screens) as well as on the server.
        event.dataPackRegistry(RARITY_BALANCE, RarityBalance.CODEC, RarityBalance.CODEC);
        event.dataPackRegistry(GEM_BALANCE, GemBalance.CODEC, GemBalance.CODEC);
    }
}
