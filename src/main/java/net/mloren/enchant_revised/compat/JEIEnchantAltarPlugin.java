package net.mloren.enchant_revised.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.block.ModBlocks;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipe;
import net.mloren.enchant_revised.recipe.ModRecipes;
import net.mloren.enchant_revised.screen.custom.EnchantAltarScreen;

import java.util.List;

@JeiPlugin
public class JEIEnchantAltarPlugin implements IModPlugin
{
    @Override
    public ResourceLocation getPluginUid()
    {
        return ResourceLocation.fromNamespaceAndPath(MainMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        registration.addRecipeCategories(new EnchantAltarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<EnchantAltarRecipe> enchantAltarRecipes = recipeManager
                .getAllRecipesFor(ModRecipes.ENCHANT_ALTAR_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(EnchantAltarRecipeCategory.ENCHANT_ALTAR_RECIPE_RECIPE_TYPE, enchantAltarRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addRecipeClickArea(EnchantAltarScreen.class, 96, 36, 21, 14,
        EnchantAltarRecipeCategory.ENCHANT_ALTAR_RECIPE_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ENCHANT_ALTAR.asItem()),
                EnchantAltarRecipeCategory.ENCHANT_ALTAR_RECIPE_RECIPE_TYPE);
    }
}
