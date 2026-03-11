package com.rave.projectbabylonmaterials.recipe;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.init.PBMRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class HammerPlatingRecipe extends CustomRecipe {
    private static final TagKey<Item> SMITHHAMMERS_TAG =
            ItemTags.create(new ResourceLocation(ProjectBabylonMaterials.MODID, "smithhammers"));
    private static final int HAMMER_DAMAGE_PER_CRAFT = 25;

    public HammerPlatingRecipe(ResourceLocation recipeId, CraftingBookCategory category) {
        super(recipeId, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean hasHammer = false;
        boolean hasSupportedIngot = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(SMITHHAMMERS_TAG)) {
                if (hasHammer) {
                    return false;
                }
                hasHammer = true;
                continue;
            }

            if (getPlateForIngot(stack.getItem()) != null) {
                if (hasSupportedIngot) {
                    return false;
                }
                hasSupportedIngot = true;
                continue;
            }

            return false;
        }

        return hasHammer && hasSupportedIngot;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, net.minecraft.core.RegistryAccess registryAccess) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty() || stack.is(SMITHHAMMERS_TAG)) {
                continue;
            }

            Item plate = getPlateForIngot(stack.getItem());
            if (plate != null) {
                return new ItemStack(plate, 2);
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty() || !stack.is(SMITHHAMMERS_TAG)) {
                continue;
            }

            ItemStack damagedHammer = stack.copy();
            damagedHammer.setCount(1);

            int newDamage = damagedHammer.getDamageValue() + HAMMER_DAMAGE_PER_CRAFT;
            if (newDamage < damagedHammer.getMaxDamage()) {
                damagedHammer.setDamageValue(newDamage);
                remaining.set(i, damagedHammer);
            }

            break;
        }

        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PBMRecipes.HAMMER_PLATING_SERIALIZER.get();
    }

    private static Item getPlateForIngot(Item ingot) {
        if (ingot == PBMItems.STEEL_INGOT.get()) {
            return PBMItems.STEEL_PLATE.get();
        }
        if (ingot == PBMItems.DRAGONSTEEL_INGOT.get()) {
            return PBMItems.DRAGONSTEEL_PLATE.get();
        }
        if (ingot == PBMItems.EVERFROST_INGOT.get()) {
            return PBMItems.EVERFROST_PLATE.get();
        }
        if (ingot == PBMItems.ETHEREAL_INGOT.get()) {
            return PBMItems.ETHEREAL_PLATE.get();
        }
        if (ingot == PBMItems.DIAMOND_INGOT.get()) {
            return PBMItems.DIAMOND_PLATE.get();
        }
        if (ingot == PBMItems.INFUSED_GOLDEN_INGOT.get()) {
            return PBMItems.INFUSED_GOLDEN_PLATE.get();
        }
        if (ingot == Items.IRON_INGOT) {
            return PBMItems.IRON_PLATE.get();
        }
        if (ingot == Items.NETHERITE_INGOT) {
            return PBMItems.NETHERITE_PLATE.get();
        }

        return null;
    }
}
