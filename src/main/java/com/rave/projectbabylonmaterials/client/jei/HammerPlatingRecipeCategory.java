package com.rave.projectbabylonmaterials.client.jei;

import com.rave.projectbabylonmaterials.init.PBMItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class HammerPlatingRecipeCategory implements IRecipeCategory<HammerPlatingJeiRecipe> {
    private static final ResourceLocation FURNACE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/furnace.png");
    private static final int WIDTH = 116;
    private static final int HEIGHT = 18;

    private final IDrawable icon;

    public HammerPlatingRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(PBMItems.IRON_SMITHHAMMER.get()));
    }

    @Override
    public mezz.jei.api.recipe.RecipeType<HammerPlatingJeiRecipe> getRecipeType() {
        return PBMJeiPlugin.HAMMER_PLATING_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("item.project_babylon_materials.iron_smithhammer");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HammerPlatingJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 0, 1)
                .addItemStacks(recipe.hammers());
        builder.addSlot(RecipeIngredientRole.INPUT, 34, 1)
                .addItemStack(recipe.ingot());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 1)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(HammerPlatingJeiRecipe recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView,
                     net.minecraft.client.gui.GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(FURNACE_TEXTURE, 58, 1, 176, 14, 24, 17);
    }

    public static List<HammerPlatingJeiRecipe> createRecipes() {
        List<ItemStack> hammers = List.of(
                new ItemStack(PBMItems.IRON_SMITHHAMMER.get()),
                new ItemStack(PBMItems.GOLDEN_SMITHHAMMER.get()),
                new ItemStack(PBMItems.DIAMOND_SMITHHAMMER.get()),
                new ItemStack(PBMItems.NETHERITE_SMITHHAMMER.get())
        );

        return List.of(
                new HammerPlatingJeiRecipe(hammers, new ItemStack(PBMItems.STEEL_INGOT.get()), new ItemStack(PBMItems.STEEL_PLATE.get(), 2)),
                new HammerPlatingJeiRecipe(hammers, new ItemStack(PBMItems.DRAGONSTEEL_INGOT.get()), new ItemStack(PBMItems.DRAGONSTEEL_PLATE.get(), 2)),
                new HammerPlatingJeiRecipe(hammers, new ItemStack(PBMItems.EVERFROST_INGOT.get()), new ItemStack(PBMItems.EVERFROST_PLATE.get(), 2)),
                new HammerPlatingJeiRecipe(hammers, new ItemStack(PBMItems.ETHEREAL_INGOT.get()), new ItemStack(PBMItems.ETHEREAL_PLATE.get(), 2)),
                new HammerPlatingJeiRecipe(hammers, new ItemStack(PBMItems.DIAMOND_INGOT.get()), new ItemStack(PBMItems.DIAMOND_PLATE.get(), 2)),
                new HammerPlatingJeiRecipe(hammers, new ItemStack(PBMItems.INFUSED_GOLDEN_INGOT.get()), new ItemStack(PBMItems.INFUSED_GOLDEN_PLATE.get(), 2)),
                new HammerPlatingJeiRecipe(hammers, new ItemStack(Items.IRON_INGOT), new ItemStack(PBMItems.IRON_PLATE.get(), 2)),
                new HammerPlatingJeiRecipe(hammers, new ItemStack(Items.NETHERITE_INGOT), new ItemStack(PBMItems.NETHERITE_PLATE.get(), 2))
        );
    }
}
