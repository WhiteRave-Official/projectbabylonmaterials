package com.rave.projectbabylonmaterials.client.jei;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.init.PBMBlocks;
import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.init.PBMRecipes;
import com.rave.projectbabylonmaterials.recipe.MagicalInfuserRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

@JeiPlugin
public class PBMJeiPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_UID =
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "jei_plugin");
    public static final RecipeType<MagicalInfuserRecipe> MAGICAL_INFUSER_RECIPE_TYPE =
            RecipeType.create(ProjectBabylonMaterials.MODID, "magical_infusing", MagicalInfuserRecipe.class);
    public static final RecipeType<HammerPlatingJeiRecipe> HAMMER_PLATING_RECIPE_TYPE =
            RecipeType.create(ProjectBabylonMaterials.MODID, "hammer_plating", HammerPlatingJeiRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new MagicalInfuserRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new HammerPlatingRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(MAGICAL_INFUSER_RECIPE_TYPE, getMagicalInfuserRecipes());
        registration.addRecipes(HAMMER_PLATING_RECIPE_TYPE, HammerPlatingRecipeCategory.createRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(PBMBlocks.MAGICAL_INFUSER_BLOCK.get()), MAGICAL_INFUSER_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(PBMItems.IRON_SMITHHAMMER.get()), HAMMER_PLATING_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(PBMItems.GOLDEN_SMITHHAMMER.get()), HAMMER_PLATING_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(PBMItems.DIAMOND_SMITHHAMMER.get()), HAMMER_PLATING_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(PBMItems.NETHERITE_SMITHHAMMER.get()), HAMMER_PLATING_RECIPE_TYPE);
    }

    private static List<MagicalInfuserRecipe> getMagicalInfuserRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) {
            return Collections.emptyList();
        }

        return level.getRecipeManager().getAllRecipesFor(PBMRecipes.MAGICAL_INFUSING_TYPE.get())
                .stream()
                .map(net.minecraft.world.item.crafting.RecipeHolder::value)
                .toList();
    }
}
