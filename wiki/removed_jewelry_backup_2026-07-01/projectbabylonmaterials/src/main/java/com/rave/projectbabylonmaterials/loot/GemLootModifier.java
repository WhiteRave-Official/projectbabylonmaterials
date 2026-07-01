package com.rave.projectbabylonmaterials.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rave.projectbabylonmaterials.gem.GemUpgradeHelper;
import com.rave.projectbabylonmaterials.gem.GemType;
import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

public class GemLootModifier extends LootModifier {
    public static final Codec<GemLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst)
            .apply(inst, GemLootModifier::new));

    private static final float GEM_ROLL_CHANCE = 0.12F;
    private static final int MAX_GEMS_PER_CHEST = 3;

    protected GemLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!LootTableFilterHelper.isGemLootTable(context.getQueriedLootTableId())) {
            return generatedLoot;
        }

        int existingGems = countGemStacks(generatedLoot);
        if (existingGems >= MAX_GEMS_PER_CHEST) {
            return generatedLoot;
        }

        RandomSource random = context.getRandom();
        for (int i = 0; i < MAX_GEMS_PER_CHEST && existingGems < MAX_GEMS_PER_CHEST; i++) {
            if (random.nextFloat() < GEM_ROLL_CHANCE) {
                generatedLoot.add(createRandomGemStack(random));
                existingGems++;
            }
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    private static int countGemStacks(ObjectArrayList<ItemStack> generatedLoot) {
        int count = 0;
        for (ItemStack stack : generatedLoot) {
            if (GemType.fromStack(stack).isPresent()) {
                count++;
            }
        }
        return count;
    }

    private static ItemStack createRandomGemStack(RandomSource random) {
        ItemStack stack = new ItemStack(PBMItems.GEM_ITEMS.get(random.nextInt(PBMItems.GEM_ITEMS.size())).get());
        ItemRarityTier rarity = ItemRarityHelper.rollRarity(random);
        ItemRarityHelper.applyRarity(stack, rarity, random);
        GemUpgradeHelper.ensureUpgradeAttempts(stack);
        return stack;
    }
}