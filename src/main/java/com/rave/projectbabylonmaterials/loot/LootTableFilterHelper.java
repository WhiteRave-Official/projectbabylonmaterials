package com.rave.projectbabylonmaterials.loot;

import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public final class LootTableFilterHelper {
    private static final Set<String> VANILLA_ELIGIBLE_CHESTS = Set.of(
            "chests/simple_dungeon",
            "chests/abandoned_mineshaft",
            "chests/desert_pyramid",
            "chests/jungle_temple",
            "chests/stronghold_corridor",
            "chests/stronghold_crossing",
            "chests/stronghold_library",
            "chests/woodland_mansion",
            "chests/underwater_ruin_small",
            "chests/underwater_ruin_big",
            "chests/buried_treasure",
            "chests/shipwreck_treasure",
            "chests/shipwreck_supply",
            "chests/shipwreck_map",
            "chests/ruined_portal",
            "chests/ancient_city",
            "chests/end_city_treasure",
            "chests/nether_bridge",
            "chests/bastion_bridge",
            "chests/bastion_hoglin_stable",
            "chests/bastion_other",
            "chests/bastion_treasure",
            "chests/pillager_outpost"
    );

    private static final Set<String> NEGATIVE_KEYWORDS = Set.of(
            "village",
            "spawn_bonus_chest",
            "camp",
            "farmer",
            "fisher",
            "house",
            "room",
            "kitchen",
            "stable",
            "cartographer",
            "weaponsmith",
            "toolsmith",
            "armorer",
            "shepherd",
            "butcher",
            "desert_house",
            "plains_house",
            "savanna_house",
            "snowy_house",
            "taiga_house"
    );

    private static final Set<String> POSITIVE_KEYWORDS = Set.of(
            "dungeon",
            "crypt",
            "temple",
            "tomb",
            "ruin",
            "mine",
            "mineshaft",
            "keep",
            "tower",
            "fortress",
            "stronghold",
            "bastion",
            "city",
            "catacomb",
            "sewer",
            "vault",
            "treasure",
            "shrine",
            "lair",
            "lab",
            "library",
            "trial",
            "portal",
            "mansion",
            "outpost",
            "pyramid",
            "shipwreck",
            "underwater"
    );

    private LootTableFilterHelper() {
    }

    public static boolean isGemLootTable(ResourceLocation lootTableId) {
        if (lootTableId == null) {
            return false;
        }

        String path = lootTableId.getPath();
        if ("minecraft".equals(lootTableId.getNamespace()) && VANILLA_ELIGIBLE_CHESTS.contains(path)) {
            return true;
        }

        if (!(path.startsWith("chests/") || path.contains("/chests/"))) {
            return false;
        }

        String loweredPath = path.toLowerCase();
        for (String negativeKeyword : NEGATIVE_KEYWORDS) {
            if (loweredPath.contains(negativeKeyword)) {
                return false;
            }
        }

        for (String positiveKeyword : POSITIVE_KEYWORDS) {
            if (loweredPath.contains(positiveKeyword)) {
                return true;
            }
        }

        return false;
    }
}