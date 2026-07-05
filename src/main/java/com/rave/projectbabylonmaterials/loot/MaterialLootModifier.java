package com.rave.projectbabylonmaterials.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class MaterialLootModifier extends LootModifier {
    public static final MapCodec<MaterialLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
            .apply(inst, MaterialLootModifier::new));

    protected MaterialLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!LootTableFilterHelper.isGemLootTable(context.getQueriedLootTableId())) {
            return generatedLoot;
        }

        RandomSource random = context.getRandom();
        rollMaterial(generatedLoot, random, com.rave.projectbabylonmaterials.init.PBMItems.PURE_TEAR.get(), 0.25F, 2, 3);
        rollMaterial(generatedLoot, random, com.rave.projectbabylonmaterials.init.PBMItems.ANCIENT_AMBER.get(), 0.18F, 2, 3);
        rollMaterial(generatedLoot, random, com.rave.projectbabylonmaterials.init.PBMItems.MAGIC_CRYSTAL.get(), 0.10F, 1, 2);
        rollMaterial(generatedLoot, random, com.rave.projectbabylonmaterials.init.PBMItems.FATE_ORB.get(), 0.07F, 1, 2);
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    private static void rollMaterial(ObjectArrayList<ItemStack> generatedLoot, RandomSource random, Item item, float chance, int minCount, int maxCount) {
        if (random.nextFloat() >= chance) {
            return;
        }

        int count = minCount + random.nextInt(maxCount - minCount + 1);
        generatedLoot.add(new ItemStack(item, count));
    }
}