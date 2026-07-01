package com.rave.projectbabylonmaterials.recipe;

import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.init.PBMRecipes;
import com.rave.projectbabylonmaterials.item.gem.GemItem;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class GemDustRecipe extends CustomRecipe {
    public GemDustRecipe(ResourceLocation recipeId, CraftingBookCategory category) {
        super(recipeId, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean hasMortar = false;
        boolean hasGem = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(PBMItems.BOWL_AND_MORTAR.get())) {
                if (hasMortar) {
                    return false;
                }
                hasMortar = true;
                continue;
            }

            if (stack.getItem() instanceof GemItem) {
                if (hasGem) {
                    return false;
                }
                hasGem = true;
                continue;
            }

            return false;
        }

        return hasMortar && hasGem;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, net.minecraft.core.RegistryAccess registryAccess) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof GemItem) {
                return new ItemStack(PBMItems.GEM_DUST.get(), getDustCount(stack));
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.hasCraftingRemainingItem()) {
                remaining.set(i, stack.getCraftingRemainingItem());
            }
        }
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PBMRecipes.GEM_DUST_SERIALIZER.get();
    }

    private static int getDustCount(ItemStack gemStack) {
        ItemRarityTier rarity = ItemRarityHelper.getRarity(gemStack).orElse(ItemRarityTier.COMMON);
        return switch (rarity) {
            case COMMON -> 2;
            case UNCOMMON -> 4;
            case RARE -> 6;
            case EPIC -> 8;
            case LEGENDARY -> 10;
        };
    }
}
