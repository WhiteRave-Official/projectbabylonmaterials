package com.rave.projectbabylonmaterials.client.jei;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.recipe.MagicalInfuserRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MagicalInfuserRecipeCategory implements IRecipeCategory<MagicalInfuserRecipe> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "textures/gui/container/magical_infuser.png");
    private static final int WIDTH = 150;
    private static final int HEIGHT = 76;

    private final IDrawable background;
    private final IDrawable icon;

    public MagicalInfuserRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(GUI_TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(com.rave.projectbabylonmaterials.init.PBMBlocks.MAGICAL_INFUSER_BLOCK.get()));
    }

    @Override
    public mezz.jei.api.recipe.RecipeType<MagicalInfuserRecipe> getRecipeType() {
        return PBMJeiPlugin.MAGICAL_INFUSER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.project_babylon_materials.magical_infuser_block");
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
    public void setRecipe(IRecipeLayoutBuilder builder, MagicalInfuserRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 68, 18)
                .addItemStacks(withCount(recipe.getTopIngredient().getItems(), recipe.getTopCount()));
        builder.addSlot(RecipeIngredientRole.INPUT, 68, 55)
                .addItemStacks(withCount(recipe.getBottomIngredient().getItems(), recipe.getBottomCount()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 125, 36)
                .addItemStack(recipe.getResult());
    }

    @Override
    public void draw(MagicalInfuserRecipe recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // JEI 19 no longer auto-draws getBackground(); render the GUI texture in draw().
        background.draw(guiGraphics, 0, 0);
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, Integer.toString(recipe.getCraftTime() / 20) + "s", 118, 61, 0x808080, false);
    }

    private static List<ItemStack> withCount(ItemStack[] stacks, int count) {
        List<ItemStack> result = new ArrayList<>(stacks.length);
        for (ItemStack stack : stacks) {
            ItemStack copy = stack.copy();
            copy.setCount(count);
            result.add(copy);
        }
        return result;
    }
}
