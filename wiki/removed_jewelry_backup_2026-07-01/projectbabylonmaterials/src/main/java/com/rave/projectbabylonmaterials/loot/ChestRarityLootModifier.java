package com.rave.projectbabylonmaterials.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rave.projectbabylonmaterials.enchantment.EnchantmentSlotHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

public class ChestRarityLootModifier extends LootModifier {
    public static final Codec<ChestRarityLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst)
            .apply(inst, ChestRarityLootModifier::new));

    protected ChestRarityLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (ItemStack stack : generatedLoot) {
            ItemRarityHelper.ensureRarity(stack, context.getRandom());
            EnchantmentSlotHelper.trimToSlotLimit(stack);
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}