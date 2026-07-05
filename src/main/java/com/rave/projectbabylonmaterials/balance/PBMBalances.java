package com.rave.projectbabylonmaterials.balance;

import com.rave.projectbabylonmaterials.gem.GemType;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Central, read-mostly snapshot of the datapack-driven balance values.
 *
 * <p>The snapshot is rebuilt from the {@link PBMBalanceRegistries} registries whenever datapacks (re)load on
 * the server ({@link OnDatapackSyncEvent}) and when the client receives the synced registries on login
 * (see {@code PBMClientBalanceSync}). Every accessor returns an {@link Optional}: callers fall back to their
 * own hardcoded defaults when an entry is absent, so behaviour matches the pre-datapack build until a pack
 * overrides a value.
 */
public final class PBMBalances {
    private static volatile Map<ItemRarityTier, RarityBalance> rarityBalances = new EnumMap<>(ItemRarityTier.class);
    private static volatile Map<GemType, GemBalance> gemBalances = new EnumMap<>(GemType.class);

    private PBMBalances() {
    }

    public static Optional<RarityBalance> rarity(ItemRarityTier tier) {
        return Optional.ofNullable(rarityBalances.get(tier));
    }

    public static Optional<GemBalance> gem(GemType type) {
        return Optional.ofNullable(gemBalances.get(type));
    }

    /**
     * The largest gem-slot count any loaded rarity can roll, used to clamp the stored slot count.
     * Falls back to {@code fallback} when no balances are loaded.
     */
    public static int maxGemSlots(int fallback) {
        int max = 0;
        for (RarityBalance balance : rarityBalances.values()) {
            int slots = balance.baseGemSlots() + (balance.gemSlotBonusChance() > 0 ? 1 : 0);
            if (slots > max) {
                max = slots;
            }
        }
        return max > 0 ? max : fallback;
    }

    public static void rebuild(RegistryAccess access) {
        if (access == null) {
            return;
        }

        Map<ItemRarityTier, RarityBalance> rarities = new EnumMap<>(ItemRarityTier.class);
        access.registry(PBMBalanceRegistries.RARITY_BALANCE).ifPresent(registry -> {
            for (Map.Entry<ResourceKey<RarityBalance>, RarityBalance> entry : registry.entrySet()) {
                ItemRarityTier tier = ItemRarityTier.byIdOrNull(entry.getKey().location().getPath());
                if (tier != null) {
                    rarities.put(tier, entry.getValue());
                }
            }
        });

        Map<GemType, GemBalance> gems = new EnumMap<>(GemType.class);
        access.registry(PBMBalanceRegistries.GEM_BALANCE).ifPresent(registry -> {
            for (Map.Entry<ResourceKey<GemBalance>, GemBalance> entry : registry.entrySet()) {
                GemType type = GemType.byRegistryNameOrNull(entry.getKey().location().getPath());
                if (type != null) {
                    gems.put(type, entry.getValue());
                }
            }
        });

        rarityBalances = rarities;
        gemBalances = gems;
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        MinecraftServer server = event.getPlayerList().getServer();
        rebuild(server.registryAccess());
    }
}
